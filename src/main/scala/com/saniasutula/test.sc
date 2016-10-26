Some("access_token=EAAQQLFDrdRIBAFLC1SI5OqbmtjwjnY84KQ9oGyZB7GeZB15lpjmKiOkQwN4qyk8NN7fHBIV7vZBJZASV9QTan9bGMv2SUUzZBTr8HxN9P4ANxODiH8MhlraDrLRlGGZAyRyz9BXi8kourfM9vpz5mYycLf94zhho0ZD&expires=5179541".split("&")
  .filter(_.contains("access_token"))
  .map(_.replace("access_token=", ""))
  .head)

val string = "access_token=EAAQQLFDrdRIBAFLC1SI5OqbmtjwjnY84KQ9oGyZB7GeZB15lpjmKiOkQwN4qyk8NN7fHBIV7vZBJZASV9QTan9bGMv2SUUzZBTr8HxN9P4ANxODiH8MhlraDrLRlGGZAyRyz9BXi8kourfM9vpz5mYycLf94zhho0ZD&expires=5179541"
val tokenString =
  for {
    p <- string.split("&")
    if p.contains("access_token")
  } yield p

"access_token=EAAQQLFDrdRIBAFLC1SI5OqbmtjwjnY84KQ9oGyZB7GeZB15lpjmKiOkQwN4qyk8NN7fHBIV7vZBJZASV9QTan9bGMv2SUUzZBTr8HxN9P4ANxODiH8MhlraDrLRlGGZAyRyz9BXi8kourfM9vpz5mYycLf94zhho0ZD&expires=5179541"
  .split("&").find(_.contains("access_token"))