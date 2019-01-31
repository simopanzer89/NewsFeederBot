package org.telegram;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class NewsFeederBot extends TelegramLongPollingBot {

	private final static long PERIOD_SEC = 86400; //24 * 60 * 60;
	private final static int TARGET_HOUR = 21;
	
	private final ScheduledExecutorService scheduler;
	private final Runnable task;
	private ScheduledFuture<?> taskHandle;
	
	private long myChatID;

	public NewsFeederBot() {
		
		scheduler = Executors.newScheduledThreadPool(1);
		
		task = new Runnable() {

			@Override
			public void run() {
				// DEBUG
				String s = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				System.out.println("Run search!" + s);
								
				runSearch();
			}
			
		};
		
		taskHandle = null;
	}
	
	@Override
	public String getBotUsername() {
		return BotConfig.BOT_NAME;
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			
			myChatID = update.getMessage().getChatId();
			//System.out.println("message received from chat ID " + String.valueOf(myChatID));
			
			long initialDelay = getSecondsUntilTarget(TARGET_HOUR);
			System.out.println(initialDelay + " seconds to the next search.");
			
			// if present, cancel old task
			if (!(taskHandle == null)) {
				taskHandle.cancel(true);
				System.out.println("CANCELLING OLD TASK.");
			}
			
			// schedule new task
			System.out.println("CREATING NEW TASK.");
			taskHandle = scheduler.scheduleAtFixedRate(task, initialDelay, PERIOD_SEC, TimeUnit.SECONDS);
			
			System.out.println("Setting automatic search every day at "+ LocalTime.of(TARGET_HOUR, 0).toString());
			//runSearch();
			
			// inform the user that automatic search has been set
			SendMessage message = new SendMessage()
					.setChatId(myChatID)
					.setText("Automatic search has been scheduled every day at " 
					+ LocalTime.of(TARGET_HOUR, 0).toString()
					+'.');
			try {
				execute(message);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
			
		}
		return;
	}

	@Override
	public String getBotToken() {
		return BotConfig.BOT_TOKEN;
	}
	
	private void runSearch() {
		
		String report = "Search report:\n\n"
				+ "keyword\t #articles\n\n";
				
		for (int j = 0; j < NewsApiParams.KEYWORD_LIST.length; j++) {
			String ch = NewsApiParams.KEYWORD_LIST[j];
			
			NewsApiQuery q = new NewsApiQuery();
			q.setKeyword(ch);
			System.out.println(q.getUrl());
			
			Vector<Article> v = q.execute();
			int c = v.size();
						
			report = report.concat(ch + "\t" + String.valueOf(c) + "\n");
			
			if (c > 0) {
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
		
		// Send the final report
		SendMessage message = new SendMessage()
				.setChatId(myChatID)
				.setText(report);
		try {
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Get time left (seconds) to the next scheduled task.
	 * Task is scheduled every day at a prefixed time (TARGET_HOUR:00:00)
	 */
	private long getSecondsUntilTarget(int targetHour) {
		
		LocalDateTime now = LocalDateTime.now();
		
		int currentHour = now.getHour();
		
		LocalDateTime target;
		if (currentHour < targetHour) {
			target = now.truncatedTo(ChronoUnit.HOURS).withHour(targetHour);
		}
		else {
			target = now.truncatedTo(ChronoUnit.HOURS).withHour(targetHour).plusDays(1); // jump to tomorrow
		}
		Duration d = Duration.between(now, target);
		
		System.out.println("current time:" + now.toString());
		System.out.println("target time:" + target.toString());
		return d.getSeconds();
	}
}
