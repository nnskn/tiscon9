package com.tiscon.domain;

import java.io.Serializable;

public class MoveMonth implements Serializable {

    private String moveMonthId;

    private String moveMonthName;

    public String getMoveMonthId() {
        return moveMonthId;
    }

    public void setMoveMonthId(String moveMonthId) {
        this.moveMonthId = moveMonthId;
    }

    public String getMoveMonthName() {
        return moveMonthName;
    }

    public void setMoveMonthName(String moveMonthName) {
        this.moveMonthName = moveMonthName;
    }
}
