/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.elections;

import java.util.HashMap;

/**
 *
 * @author merxbj
 */
public class LocalQuorumChecker implements QuorumChecker {

    long totalVoteCount;
    
    public LocalQuorumChecker(HashMap<Party, Long> votes) {
        totalVoteCount = sumVotes(votes);
    }
    
    @Override
    public boolean passed(long voteCount) {
        return (voteCount / (double) totalVoteCount) >= 0.05; // 5% minimum votes
    }

    private long sumVotes(HashMap<Party, Long> votes) {
        long total = 0L;
        for (long voteCount : votes.values()) {
            total += voteCount;
        }
        return total;
    }
}
