/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.elections;

/**
 *
 * @author merxbj
 */
public class DummyQuorumChecker implements QuorumChecker {

    @Override
    public boolean passed(long voteCount) {
        return true;
    }
    
}
