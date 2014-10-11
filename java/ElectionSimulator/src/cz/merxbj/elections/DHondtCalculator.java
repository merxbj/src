/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.elections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author merxbj
 */
public class DHondtCalculator {

    private final List<Party> parties;
    private final int availableMandates;
    private final QuorumChecker quorum;
    
    public DHondtCalculator(List<Party> parties, int availableMandates, QuorumChecker quorum) {
        this.parties = parties;
        this.availableMandates = availableMandates;
        this.quorum = quorum;
    }

    public HashMap<Party, Integer> calculate(HashMap<Party, Long> votes) {
        
        HashMap<Party, Integer> results = initializeResults();
        
        List<PartyVotes> correctedVotes = initializeCorrectedVotes(votes);
        for (int round = 0; round < availableMandates; round++) {

            updateCorrectedVotes(correctedVotes, results);

            Collections.sort(correctedVotes);
            PartyVotes top = correctedVotes.get(0);
            Party winner = top.getParty();

            addMandate(results, winner);
        }
        
        return results;
    }

    private List<PartyVotes> initializeCorrectedVotes(HashMap<Party, Long> votes) {
        List<PartyVotes> correctedVotes = new ArrayList<>();
        for (Party party : parties) {
            long voteCount = votes.containsKey(party) ? votes.get(party) : 0L;
            if (quorum.passed(voteCount)) {
                correctedVotes.add(new PartyVotes(party, voteCount));
            }
        }
        return correctedVotes;
    }

    private void addMandate(HashMap<Party, Integer> results, Party winner) {
        if (!results.containsKey(winner)) {
            results.put(winner, 1);
        } else {
            results.put(winner, 1 + results.get(winner));
        }
    }

    private void updateCorrectedVotes(List<PartyVotes> correctedVotes, HashMap<Party, Integer> results) {
        for (PartyVotes votes : correctedVotes) {
            votes.correctVotes(results.get(votes.getParty()));
        }
    }

    private HashMap<Party, Integer> initializeResults() {
        HashMap<Party, Integer> results = new HashMap<>();
        for (Party party : parties) {
            results.put(party, 0);
        }
        return results;
    }
    
    private class PartyVotes implements Comparable<PartyVotes> {

        private final Party party;
        private final Long originalVotes;
        private Long correctedVotes;

        public PartyVotes(Party party, long votes) {
            this.party = party;
            this.originalVotes = votes;
            this.correctedVotes = 0L;
        }

        public void correctVotes(int mandates) {
            correctedVotes = (long)Math.ceil(originalVotes / (mandates + 1.0));
        }
        
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 47 * hash + Objects.hashCode(this.party);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PartyVotes other = (PartyVotes) obj;
            return Objects.equals(this.party, other.party);
        }
        
        public final Party getParty() {
            return party;
        }
         
        @Override
        public int compareTo(PartyVotes other) {
            if (other == null) {
                return -1;
            }
            
            return other.correctedVotes.compareTo(correctedVotes);
        }
        
    }
}
