package com.smartlearning.assistant.agent.tools;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StudyPlannerTool {

    public record ScheduleRequest(String subject, int dailyMinutes, int targetDays) {}

    public record DailyPlan(int day, String focus, int minutes) {}

    public record ScheduleResponse(String subject, int totalMinutes, List<DailyPlan> dailySchedules) {}

    public record ScoreTargetRequest(int currentScore, int targetScore, int weeksRemaining) {}

    public record ScoreTargetResponse(int gap, double weeklyPointsNeeded, String recommendation) {}

    public ScheduleResponse generateStudySchedule(ScheduleRequest request) {
        int days = Math.max(1, request.targetDays());
        int minutes = Math.max(15, request.dailyMinutes());
        List<DailyPlan> list = new ArrayList<>();

        for (int i = 1; i <= days; i++) {
            String focus;
            if (i == 1) {
                focus = "基础知识梳理与核心概念复习";
            } else if (i == days) {
                focus = "全真模拟测试与错题总结";
            } else if (i % 2 == 0) {
                focus = "重点难点专题练习与代码/公式推导";
            } else {
                focus = "历年真题实战与题型归纳";
            }
            list.add(new DailyPlan(i, focus, minutes));
        }

        return new ScheduleResponse(request.subject(), days * minutes, list);
    }

    public ScoreTargetResponse calculateScoreTarget(ScoreTargetRequest request) {
        int gap = Math.max(0, request.targetScore() - request.currentScore());
        int weeks = Math.max(1, request.weeksRemaining());
        double weeklyNeeded = (double) gap / weeks;

        String recommendation;
        if (weeklyNeeded <= 2.0) {
            recommendation = "目标节奏稳健，保持日常刷题与错题巩固即可。";
        } else if (weeklyNeeded <= 5.0) {
            recommendation = "需要集中攻克薄弱模块，建议每天增加 30 分钟专项突破。";
        } else {
            recommendation = "提分跨度较大，建议制定冲刺计划并配合 AI 助手进行每日核心考点诊断。";
        }

        return new ScoreTargetResponse(gap, weeklyNeeded, recommendation);
    }
}
