package com.smartlearning.assistant.agent;

import static org.junit.jupiter.api.Assertions.*;

import com.smartlearning.assistant.agent.tools.StudyPlannerTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudyPlannerToolTest {

    private StudyPlannerTool studyPlannerTool;

    @BeforeEach
    void setUp() {
        studyPlannerTool = new StudyPlannerTool();
    }

    @Test
    void shouldGenerateValidStudySchedule() {
        StudyPlannerTool.ScheduleRequest request = new StudyPlannerTool.ScheduleRequest("Java Concurrency", 60, 7);
        StudyPlannerTool.ScheduleResponse response = studyPlannerTool.generateStudySchedule(request);

        assertNotNull(response);
        assertEquals("Java Concurrency", response.subject());
        assertEquals(7, response.dailySchedules().size());
        assertEquals(420, response.totalMinutes());
    }

    @Test
    void shouldCalculateScoreTargetCorrectly() {
        StudyPlannerTool.ScoreTargetRequest request = new StudyPlannerTool.ScoreTargetRequest(60, 80, 4);
        StudyPlannerTool.ScoreTargetResponse response = studyPlannerTool.calculateScoreTarget(request);

        assertNotNull(response);
        assertEquals(20, response.gap());
        assertEquals(5.0, response.weeklyPointsNeeded());
        assertFalse(response.recommendation().isEmpty());
    }
}
