package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiEventTest {
    private MultiEvent m1;
    private MultiEvent m2;
    private MultiEvent m3;

    @BeforeEach
    void runBefore() {
        m1 = new MultiEvent("m1", LocalDate.of(2000, 9, 4),
                LocalTime.of(9, 30), LocalTime.of(10, 45));
        m2 = new MultiEvent("m2", LocalDate.of(1950, 7, 16),
                LocalTime.of(22, 30), LocalTime.of(23, 45));
        m3 = new MultiEvent("m3", LocalDate.of(2049, 2, 26),
                LocalTime.of(7, 50), LocalTime.of(14, 17));
    }

    private void addLinks() {
        m1.addLink(m2);
        m3.addLink(m1);
    }

    @Test
    void testAddLink() {
        addLinks();
        List<String> m1OtherNames = m1.getOtherNames();
        List<String> m2OtherNames = m2.getOtherNames();
        List<String> m3OtherNames = m3.getOtherNames();
        assertEquals(m1OtherNames.size(), 1);
        assertEquals(m2OtherNames.size(), 0);
        assertEquals(m3OtherNames.size(), 2);
        assertFalse(m1OtherNames.contains("m1"));
        assertTrue(m1OtherNames.contains("m2"));
        assertFalse(m1OtherNames.contains("m3"));
        assertFalse(m2OtherNames.contains("m1"));
        assertFalse(m2OtherNames.contains("m2"));
        assertFalse(m2OtherNames.contains("m3"));
        assertTrue(m3OtherNames.contains("m1"));
        assertTrue(m3OtherNames.contains("m2"));
        assertFalse(m3OtherNames.contains("m3"));
    }

    @Test
    void testGetMergedName() {
        addLinks();
        assertEquals("m1, m2", m1.getMergedName());
        assertEquals("m2", m2.getMergedName());
        assertEquals("m3, m1, m2", m3.getMergedName());
    }

    @Test
    void testSplit() {
        addLinks();
        List<MultiEvent> m1Split = m1.split();
        List<MultiEvent> m2Split = m2.split();
        List<MultiEvent> m3Split = m3.split();
        assertEquals(0, m1.getOtherNames().size());
        assertEquals(0, m2.getOtherNames().size());
        assertEquals(0, m3.getOtherNames().size());
        assertEquals(1, m1Split.size());
        assertEquals(0, m2Split.size());
        assertEquals(2, m3Split.size());
    }

    @Test
    void testMergeSplitEvents() {
        List<MultiEvent> sortedEvents = new ArrayList<>();
        MultiEvent m4 = new MultiEvent("m4", m2.getDate(), m2.getStartTime(), m2.getEndTime());
        MultiEvent m5 = new MultiEvent("m5", m2.getDate(), m2.getStartTime(), m2.getEndTime());
        MultiEvent m6 = new MultiEvent("m6", m3.getDate(), m3.getStartTime(), m3.getEndTime());
        sortedEvents.add(m2);
        sortedEvents.add(m5);
        sortedEvents.add(m4);
        sortedEvents.add(m1);
        sortedEvents.add(m6);
        sortedEvents.add(m3);
        assertEquals(6, sortedEvents.size());
        MultiEvent.mergeEvents(sortedEvents);
        assertEquals(3, sortedEvents.size());
        assertEquals("m2", sortedEvents.get(0).getName());
        assertEquals("m1", sortedEvents.get(1).getName());
        assertEquals("m6", sortedEvents.get(2).getName());
        MultiEvent.splitEvents(sortedEvents);
        assertEquals(6, sortedEvents.size());
        assertEquals("m2", sortedEvents.get(0).getName());
        assertEquals("m5", sortedEvents.get(1).getName());
        assertEquals("m4", sortedEvents.get(2).getName());
        assertEquals("m1", sortedEvents.get(3).getName());
        assertEquals("m6", sortedEvents.get(4).getName());
        assertEquals("m3", sortedEvents.get(5).getName());
    }
}
