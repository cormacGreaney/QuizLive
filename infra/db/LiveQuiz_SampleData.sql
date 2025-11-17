USE livequiz_test;

DELETE FROM participant_score;
DELETE FROM participant;
DELETE FROM quiz_session;
DELETE FROM question_option;
DELETE FROM question;
DELETE FROM quiz;
DELETE FROM user_account;

INSERT INTO user_account (email, display_name, role)
VALUES 
  ('admin@example.com', 'Quiz Master', 'ADMIN'),
  ('player1@example.com', 'Player One', 'PLAYER');

INSERT INTO quiz (title, description, owner_user_id)
VALUES ('Science Quiz', 'Test your science knowledge!', 1);

INSERT INTO question (quiz_id, type, text, correct_payload, points, ord)
VALUES 
  (1, 'MCQ_SINGLE', 'What planet is closest to the sun?', JSON_ARRAY('A'), 10, 1),
  (1, 'MCQ_SINGLE', 'What gas is most abundant in our atmosphere?', JSON_ARRAY('B'), 10, 2);

INSERT INTO question_option (question_id, option_key, text, ord)
VALUES 
  (1, 'A', 'Mercury', 1),
  (1, 'B', 'Venus', 2),
  (1, 'C', 'Earth', 3),
  (1, 'D', 'Mars', 4),
  (2, 'A', 'Oxygen', 1),
  (2, 'B', 'Carbon Dioxide', 2),
  (2, 'C', 'Nitrogen', 3),
  (2, 'D', 'Helium', 4);

INSERT INTO quiz_session (quiz_id, host_user_id, status, public_code)
VALUES (1, 1, 'LIVE', 'QUIZ123');

INSERT INTO participant (quiz_session_id, user_id, nickname)
VALUES (1, 2, 'PlayerOne');

INSERT INTO participant_score (participant_id, quiz_session_id, total_points)
VALUES (1, 1, 50);

SELECT * FROM v_leaderboard;
