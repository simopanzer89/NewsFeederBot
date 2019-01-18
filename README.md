# NewsFeederBot
A Java Telegram bot to get custom news feed.

Powered by NewsApi https://newsapi.org/

Based on the TelegramBots Java library by rubenlagus
https://github.com/rubenlagus/TelegramBots

# Description
This bot queries a set of news websites for a set of keywords and returns the search results to the user, if any.
Both the set of news sources and the set of keywords are user-defined.
The bot only searches for news published in the last 24h.
The results are returned to the user in the form of a message containing title, description, and URL of the article.

# Usage
You will need a key for NewsApi (free registration) https://newsapi.org/ and to create a Telegram bot (@botfather).

Clone the repo, open with Eclipse and link the TelegramBots jar (Add external jar) e.g. https://github.com/rubenlagus/TelegramBots/releases v4.1.

The user entry points are:
* BotConfig.java: to set bot configuration parameters
* NewsApiParams.java: to set-up NewsApi query parameters (keywords and sources)

Run Main.java as Java application.

Telegram side:
To run the search, just send the bot a message containing any text.
