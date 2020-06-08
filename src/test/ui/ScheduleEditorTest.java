package ui;

import model.ScheduleTest;
import model.SettingsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScheduleEditorTest extends ScheduleTest {

    private ScheduleEditor e1;

    @BeforeEach
    void runBefore() {
        Initialize();
        e1 = new ScheduleEditor();
        e1.setSaveFile(SettingsTest.TEST_SAVE_FILE);
    }

    @Test
    void testSetSchedule() {
        e1.setSchedule(s1);
        assertEquals(s1, e1.getSchedule());
    }

    @Test
    void testSave() {
        assertEquals(0, e1.getScheduleSize());
        e1.save();
        addManyToSchedule();
        e1.setSchedule(s1);
        assertEquals(15, e1.getScheduleSize());
        e1.load();
        assertEquals(0, e1.getScheduleSize());
    }

    @Test
    void testLoad() {
        addManyToSchedule();
        e1.setSchedule(s1);
        assertEquals(15, e1.getScheduleSize());
        e1.save();
        e1.clearSchedule();
        assertEquals(0, e1.getScheduleSize());
        e1.load();
        assertEquals(15, e1.getScheduleSize());
    }
}
