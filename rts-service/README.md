At the moment. just tested the functionality of live updates between clients, will work on implementing further functionalities.

need to use docker,start the app and when its running give the command in the terminal:
docker run -d --name redis -p 6379:6379 redis
to start redis

run RtsApplication, and can use quiz-html with multiple instances to see the real time communication

the html file quiz test its a very quick mock up, results generate random answers - basically just checks that the real time info is updated across every instance of it.

docker exec -it redis redis-cli
-command to open redis in terminal/powershell
then run,
ZREVRANGE leaderboard:quiz1 0 -1 WITHSCORES
to check the info is been saved to redis

In short to run,
1.start docker
2.run redis : docker run -d --name redis -p 6379:6379 redis
3. run RtsApplication class
4. quiz-client.html, open in browser
5. If you want to check the redis data, in terminal, : docker exec -it redis redis-cli 
, then run : ZREVRANGE leaderboard:quiz1 0 -1 WITHSCORES

