package com.move.move.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Moves {
    private Map<Integer, List<Integer>> moves;

    public Moves(){
        this.moves = new HashMap<>();
    }

    public Map<Integer, List<Integer>> getMoves() {
        return moves;
    }

    public void setMoves(Integer dayOfWork, List<Integer> listStuffPerDay) {
        this.moves.put(dayOfWork, listStuffPerDay);
    }
}
