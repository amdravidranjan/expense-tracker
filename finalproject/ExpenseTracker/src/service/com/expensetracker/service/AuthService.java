package com.expensetracker.service;

import com.expensetracker.domain.model.Family;
import com.expensetracker.domain.model.User;
import com.expensetracker.domain.util.Ids;
import com.expensetracker.domain.util.Validation;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class AuthService {

    public User login(String u, String p) {
        Validation.requireNonBlank(u, "Username");
        Validation.requireNonBlank(p, "Password");
        return DataStore.users().values().stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(u))
                .findFirst()
                .filter(user -> user.getPasswordHash().equals(hash(p)))
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));
    }

    public User register(String n, String u, String p) {
        Validation.requireNonBlank(n, "Name");
        Validation.requireNonBlank(u, "Username");
        Validation.requireNonBlank(p, "Password");
        if (DataStore.users().values().stream().anyMatch(user -> user.getUsername().equalsIgnoreCase(u))) {
            throw new IllegalArgumentException("Username already taken.");
        }
        User newUser = new User(Ids.newId("USR"), n, u, hash(p));
        DataStore.users().put(newUser.getId(), newUser);
        DataStore.saveUsers();
        return newUser;
    }

    public Family createFamily(User c, String fName) {
        Validation.requireNonBlank(fName, "Family Name");
        if (c.getFamilyId() != null) {
            throw new IllegalArgumentException("You are already in a family.");
        }
        if (c.getPendingJoinRequestForFamilyId() != null) {
            throw new IllegalArgumentException("You have a pending join request. Cancel it before creating a new family.");
        }
        Family f = new Family(Ids.newId("FAM"), fName);
        f.addMember(c.getId());
        f.addAdmin(c.getId());
        c.setFamilyId(f.getId());
        DataStore.families().put(f.getId(), f);
        DataStore.users().put(c.getId(), c);
        DataStore.saveFamilies();
        DataStore.saveUsers();
        return f;
    }

    public void joinFamily(User u, String fId) {
        Validation.requireNonBlank(fId, "Family ID");
        if (u.getFamilyId() != null) {
            throw new IllegalArgumentException("You are already in a family.");
        }
        if (u.getPendingJoinRequestForFamilyId() != null) {
            throw new IllegalArgumentException("You already have a pending join request.");
        }
        Family f = DataStore.families().get(fId.toUpperCase());
        if (f == null) {
            throw new IllegalArgumentException("Family ID not found.");
        }
        f.addPendingRequest(u.getId());
        u.setPendingJoinRequestForFamilyId(f.getId());
        DataStore.saveFamilies();
        DataStore.saveUsers();
    }

    public void cancelJoinRequest(User u) {
        String pendingId = u.getPendingJoinRequestForFamilyId();
        if (pendingId == null) {
            return;
        }
        Family f = DataStore.families().get(pendingId);
        if (f != null) {
            f.removePendingRequest(u.getId());
            DataStore.saveFamilies();
        }
        u.setPendingJoinRequestForFamilyId(null);
        DataStore.saveUsers();
    }

    public void approveRequest(User u, Family f) {
        f.removePendingRequest(u.getId());
        f.addMember(u.getId());
        u.setFamilyId(f.getId());
        u.setPendingJoinRequestForFamilyId(null);
        DataStore.saveFamilies();
        DataStore.saveUsers();
    }

    public void denyRequest(User u, Family f) {
        f.removePendingRequest(u.getId());
        u.setPendingJoinRequestForFamilyId(null);
        DataStore.saveFamilies();
        DataStore.saveUsers();
    }

    public void leaveFamily(User u, Family f) {
        if (f.isAdmin(u.getId()) && f.getAdminUserIds().size() == 1 && f.getMemberUserIds().size() > 1) {
            throw new IllegalStateException("You are the last admin. Promote another member first.");
        }
        f.removeMember(u.getId());
        f.removeAdmin(u.getId());
        u.setFamilyId(null);
        if (f.getMemberUserIds().isEmpty()) {
            DataStore.families().remove(f.getId());
        }
        DataStore.saveFamilies();
        DataStore.saveUsers();
    }

    private String hash(String p) {
        try {
            MessageDigest d = MessageDigest.getInstance("SHA-256");
            byte[] h = d.digest(p.getBytes(StandardCharsets.UTF_8));
            StringBuilder s = new StringBuilder(2 * h.length);
            for (byte b : h) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    s.append('0');
                }
                s.append(hex);
            }
            return s.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}