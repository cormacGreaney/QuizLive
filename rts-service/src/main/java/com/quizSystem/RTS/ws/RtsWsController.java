package com.quizSystem.RTS.ws;

import com.quizSystem.RTS.service.RedisTestService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RtsWsController {

    private final RedisTestService redisTestService;

    // Client sends to: /app/answer
    // All clients subscribe to: /topic/leaderboard
    //will be changing all of this when integrating with other services

    @MessageMapping("answer")
    @SendTo("/topic/leaderboard")
    public OutMsg submitAnswer(InMsg msg) {
        String quizId = "quiz1";
        String player = msg.getText();   // using "text" as player name

        int points = (int) (Math.random() * 90) + 10;  // random now just for testing functionality
        redisTestService.addScore(quizId, player, points);

        var top = redisTestService.getTopPlayers(quizId, 5);

        StringBuilder sb = new StringBuilder();
        sb.append("🏆 Leaderboard Update 🏆\n");
        sb.append(player).append(" gained ").append(points).append(" points!\n\n");

        int rank = 1;
        for (var entry : top) {
            sb.append(rank++)
                    .append(". ")
                    .append(entry.getValue())
                    .append(" — ")
                    .append(String.format("%.0f pts", entry.getScore()))
                    .append("\n");
        }

        return new OutMsg(sb.toString());
    }


    @Data
    public static class InMsg {
        private String text; // player name for now
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OutMsg {
        private String text;
    }
}

