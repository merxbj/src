/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import cz.merxbj.elections.DHondtCalculator;
import cz.merxbj.elections.DummyQuorumChecker;
import cz.merxbj.elections.LocalQuorumChecker;
import cz.merxbj.elections.Party;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author merxbj
 */
public class DHondtCalculatorTest {
    
    public DHondtCalculatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testWikiSimpleExample() {
        Party a = new Party("Strana A");
        Party b = new Party("Strana B");
        Party c = new Party("Strana C");
        
        List<Party> parties = new ArrayList<>();
        parties.add(a);
        parties.add(b);
        parties.add(c);
        
        HashMap<Party, Long> votes = new HashMap<>();
        votes.put(a, 100L);
        votes.put(b, 60L);
        votes.put(c, 45L);
        
        DHondtCalculator calc = new DHondtCalculator(parties, 3, new LocalQuorumChecker(votes));
        HashMap<Party, Integer> results = calc.calculate(votes);
        
        assertEquals(3, results.size());

        assertTrue(results.containsKey(a));
        assertTrue(results.containsKey(b));
        assertTrue(results.containsKey(c));
        
        assertEquals(2, (int) results.get(a));
        assertEquals(1, (int) results.get(b));
        assertEquals(0, (int) results.get(c));
    }
    
    @Test
    public void testWikiRealExample() {
        Party ods = new Party("ODS");
        Party cssd = new Party("CSSD");
        Party kscm = new Party("KSCM");
        Party sz = new Party("SZ");
        Party kducsl = new Party("KDU-CSL");
        
        List<Party> parties = new ArrayList<>();
        parties.add(ods);
        parties.add(cssd);
        parties.add(kscm);
        parties.add(sz);
        parties.add(kducsl);
        
        HashMap<Party, Long> votes = new HashMap<>();
        votes.put(ods, 83647L);
        votes.put(cssd, 63181L);
        votes.put(kscm, 24823L);
        votes.put(sz, 20646L);
        votes.put(kducsl, 9131L);
        
        DHondtCalculator calc = new DHondtCalculator(parties, 8, new DummyQuorumChecker());
        HashMap<Party, Integer> results = calc.calculate(votes);
        
        assertEquals(5, results.size());

        assertTrue(results.containsKey(ods));
        assertTrue(results.containsKey(cssd));
        assertTrue(results.containsKey(kscm));
        assertTrue(results.containsKey(sz));
        assertTrue(results.containsKey(kducsl));
        
        assertEquals(4, (int) results.get(ods));
        assertEquals(3, (int) results.get(cssd));
        assertEquals(1, (int) results.get(kscm));
        assertEquals(0, (int) results.get(sz));
        assertEquals(0, (int) results.get(kducsl));
    }
    
    @Test
    public void test2014ChomutovCity() {
        Party ns = new Party("NOVY SEVER");
        Party cs = new Party("Ceska Suverenita");
        Party pc = new Party("Pro Chomutov");
        Party sso = new Party("Strana Svobodnych Obcanu");
        Party cssd = new Party("CSSD");
        Party ano2011 = new Party("ANO 2011");
        Party kscm = new Party("KSCM");
        Party ods = new Party("ODS");
        Party nbnd = new Party("Ne Bruselu - Nar. demokracie");
        Party no = new Party("Nespokojeni Obcane");
        Party top09 = new Party("TOP 09");
        Party upsaz = new Party("Unie pro sport a zdravi");
        Party or = new Party("Otevrena radnice");
        
        List<Party> parties = new ArrayList<>();
        parties.add(ns);
        parties.add(cs);
        parties.add(pc);
        parties.add(sso);
        parties.add(cssd);
        parties.add(ano2011);
        parties.add(kscm);
        parties.add(ods);
        parties.add(nbnd);
        parties.add(no);
        parties.add(top09);
        parties.add(upsaz);
        parties.add(or);
        
        HashMap<Party, Long> votes = new HashMap<>();
        votes.put(ns, 40318L);
        votes.put(cs, 1372L);
        votes.put(pc, 63821L);
        votes.put(sso, 7588L);
        votes.put(cssd, 60548L);
        votes.put(ano2011, 57476L);
        votes.put(kscm, 49470L);
        votes.put(ods, 14645L);
        votes.put(nbnd, 210L);
        votes.put(no, 12802L);
        votes.put(top09, 16648L);
        votes.put(upsaz, 6086L);
        votes.put(or, 5278L);
        
        DHondtCalculator calc = new DHondtCalculator(parties, 35, new LocalQuorumChecker(votes));
        HashMap<Party, Integer> results = calc.calculate(votes);
        
        assertEquals(13, results.size());

        assertTrue(results.containsKey(ns));
        assertTrue(results.containsKey(cs));
        assertTrue(results.containsKey(pc));
        assertTrue(results.containsKey(sso));
        assertTrue(results.containsKey(cssd));
        assertTrue(results.containsKey(ano2011));
        assertTrue(results.containsKey(kscm));
        assertTrue(results.containsKey(ods));
        assertTrue(results.containsKey(nbnd));
        assertTrue(results.containsKey(no));
        assertTrue(results.containsKey(top09));
        assertTrue(results.containsKey(upsaz));
        assertTrue(results.containsKey(or));
        
        assertEquals(5, (int) results.get(ns));
        assertEquals(0, (int) results.get(cs));
        assertEquals(8, (int) results.get(pc));
        assertEquals(0, (int) results.get(sso));
        assertEquals(8, (int) results.get(cssd));        
        assertEquals(8, (int) results.get(ano2011));
        assertEquals(6, (int) results.get(kscm));
        assertEquals(0, (int) results.get(ods));
        assertEquals(0, (int) results.get(nbnd));
        assertEquals(0, (int) results.get(no));        
        assertEquals(0, (int) results.get(top09));
        assertEquals(0, (int) results.get(upsaz));
        assertEquals(0, (int) results.get(or));
    }
}
