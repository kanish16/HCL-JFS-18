package com.training.librarywaitlist.model;

public class WaitlistRequest implements Comparable<WaitlistRequest> {
    private final String patronName;
    private final MemberType memberType;
    private final long timestamp;

    public WaitlistRequest(String patronName, MemberType memberType) {
        this.patronName = patronName;
        this.memberType = memberType;
        this.timestamp = System.nanoTime();
    }

    public String getPatronName() {
        return patronName;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    @Override
    public int compareTo(WaitlistRequest other) {
        int roleComparison = this.memberType.compareTo(other.memberType);
        if (roleComparison != 0) {
            return roleComparison;
        }
        return Long.compare(this.timestamp, other.timestamp);
    }

    @Override
    public String toString() {
        return patronName + " [" + memberType + "]";
    }
}