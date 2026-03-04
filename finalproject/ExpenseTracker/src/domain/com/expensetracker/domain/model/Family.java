package com.expensetracker.domain.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Family implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String id, name;
    private Set<String> adminUserIds, memberUserIds, pendingRequestUserIds;
    private final Budget budget = new Budget();

    public Family(String id, String name) {
        this.id = id;
        this.name = name;
        this.adminUserIds = new HashSet<>();
        this.memberUserIds = new HashSet<>();
        this.pendingRequestUserIds = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Budget getBudget() {
        return budget;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (adminUserIds == null) {
            adminUserIds = new HashSet<>();
        }
        if (memberUserIds == null) {
            memberUserIds = new HashSet<>();
        }
        if (pendingRequestUserIds == null) {
            pendingRequestUserIds = new HashSet<>();
        }
    }

    public Set<String> getAdminUserIds() {
        return Collections.unmodifiableSet(adminUserIds);
    }

    public Set<String> getMemberUserIds() {
        return Collections.unmodifiableSet(memberUserIds);
    }

    public Set<String> getPendingRequestUserIds() {
        return Collections.unmodifiableSet(pendingRequestUserIds);
    }

    public boolean isAdmin(String userId) {
        return adminUserIds.contains(userId);
    }

    public void addMember(String userId) {
        memberUserIds.add(userId);
    }

    public void removeMember(String userId) {
        memberUserIds.remove(userId);
    }

    public void addAdmin(String userId) {
        adminUserIds.add(userId);
    }

    public void removeAdmin(String userId) {
        adminUserIds.remove(userId);
    }

    public void addPendingRequest(String userId) {
        pendingRequestUserIds.add(userId);
    }

    public void removePendingRequest(String userId) {
        pendingRequestUserIds.remove(userId);
    }
}