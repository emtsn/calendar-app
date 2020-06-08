package model;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class SaveLoadSystemTest {

    @Test
    void testSaveFail() {
        ScheduleContainer s = new ScheduleContainer();
        try {
            SaveLoadSystem.saveWithJackson(s, "");
            fail();
        } catch (IOException e) {

        }
    }

    @Test
    void testLoadFail() {
        try {
            SaveLoadSystem.loadWithJackson("", ScheduleContainer.class);
            fail();
        } catch (IOException e) {

        }
    }
}
