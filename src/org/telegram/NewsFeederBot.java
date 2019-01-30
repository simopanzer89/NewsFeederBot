package org.telegram;
import java.util.Vector;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class NewsFeederBot extends TelegramLongPollingBot {

	private long myChatID;
	
	@Override
	public String getBotUsername() {
		return BotConfig.BOT_NAME;
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			
			myChatID = update.getMessage().getChatId();
			//System.out.println("message received from chat ID " + String.valueOf(myChatID));
			
			runSearch();			
		}
		return;
	}

	@Override
	public String getBotToken() {
		return BotConfig.BOT_TOKEN;
	}
	
	private void runSearch() {
		
		for (int j = 0; j < NewsApiParams.KEYWORD_LIST.length; j++) {
			String ch = NewsApiParams.KEYWORD_LIST[j];
			
			NewsApiQuery q = new NewsApiQuery();
			q.setKeyword(ch);
			System.out.println(q.getUrl());
			
			Vector<Article> v = q.execute();
			int c = v.size();
			if (c == 0) {
				// send a message to announce no news were found.
				SendMessage message = new SendMessage()
						.setChatId(myChatID)
						.setText("No news found for " + ch + ".");
				try {
					execute(message);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			}
			else {
				// send an intro message to announce news are coming
				SendMessage messageIntro = new SendMessage()
						.setChatId(myChatID)
						.setText("Found news for " + ch + "!");
				try {
					execute(messageIntro);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
				
				// send the news
				for (int i = 0; i < c; i++) {
					Article a = v.get(i);

					SendMessage message = new SendMessage()
							.setChatId(myChatID)
							.setText(a.getTitle() + "\n\n" + 
									a.getDescription() + "\n" + 
									a.getUrl());
					try {
						execute(message);
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}

				}
			}
		}
	}

}
