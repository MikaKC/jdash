package com.github.alex1304.jdash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import com.github.alex1304.jdash.client.AuthenticatedGDClient;
import com.github.alex1304.jdash.client.GDClientBuilder;
import com.github.alex1304.jdash.client.GDClientBuilder.Credentials;
import com.github.alex1304.jdash.entity.*;
import com.github.alex1304.jdash.util.GDPaginator;
import com.github.alex1304.jdash.util.CommentSortMode;
import com.github.alex1304.jdash.util.LevelSearchFilters;

import com.github.alex1304.jdash.util.LeaderboardType;
import reactor.core.publisher.Mono;

public class TestMain {
	
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Please provide GD account login details in arguments");
			return;
		}
		AuthenticatedGDClient client = GDClientBuilder.create()
				.buildAuthenticated(new Credentials(args[0], args[1]))
				.block();
		
		Mono.when(
		
		client.getUserByAccountId(98006)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Get user 98006", o)),

		client.getCommentsForUser(855735, 0)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Get comments of user 855735", o)),

		client.searchUser("RobTop")
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Search user RobTop", o)),
		
		client.getLevelById(10565740)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Get level 10565740", o)),
		
		client.searchLevels("bloodbath", LevelSearchFilters.create(), 0)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Search levels Bloodbath", o)),
		
		client.searchLevels("sonic wave", LevelSearchFilters.create().withDifficulties(EnumSet.of(Difficulty.HARD)), 0)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Search levels Sonic wave, filter: Difficulty.HARD", o)),
		
		client.getLevelById(52637920)
			.doOnError(Throwable::printStackTrace)
			.flatMap(GDLevel::download)
			.doOnSuccess(o -> printResult("Download level 52637920", o)),

		client.getCommentsForLevel(49994214, CommentSortMode.MOST_LIKED, 0)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Get comments of level 49994214, mode: Most Liked", o)),
		
		client.getDailyLevel()
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Get Daily level info", o)),
		
		client.getWeeklyDemon()
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Get Weekly demon info", o)),
		
		client.getDailyLevel()
			.flatMap(GDTimelyLevel::getLevel)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Get Daily level download", o)),
		
		client.getWeeklyDemon()
			.flatMap(GDTimelyLevel::getLevel)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Get Weekly demon download", o)),
		
		client.browseAwardedLevels(LevelSearchFilters.create(), 0)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Browse Awarded section", o)),
		
		client.browseRecentLevels(LevelSearchFilters.create(), 0)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Browse Recent section", o)),
		
		client.browseMagicLevels(LevelSearchFilters.create(), 0)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Browse Magic section", o)),
		
		client.browseTrendingLevels(LevelSearchFilters.create(), 0)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Browse Trending section", o)),
		
		client.browseFeaturedLevels(0)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Browse Featured section", o)),
		
		client.browseHallOfFameLevels(0)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Browse Hall of Fame", o)),
		
		client.getLevelsByUser(client.getUserByAccountId(98006).block(), 0)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Get levels from Alex1304", o)),
		
		client.browseAwardedLevels(LevelSearchFilters.create(), 0)
			.flatMap(GDPaginator::goToNextPage)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Browse second page of Awarded section", o)),
		
		client.browseFollowedIds(LevelSearchFilters.create(), new ArrayList<>(Arrays.asList(98006L, 71L)), 0)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Following Alex1304 and RobTop", o)),
		
		client.getPrivateMessages(0)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Private messages", o)),
		
		client.getPrivateMessages(0)
			.map(paginator -> paginator.asList().get(0))
			.flatMap(GDMessage::getBody)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("First private message content", o)),
		
		client.sendPrivateMessage(client.searchUser("Alex1304").block(), "Test", "Hello world!")
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Send message", "Message sent!")),

		client.getLeaderboard(LeaderboardType.FRIENDS, 50)
			.collectList()
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("My friend ranking", o)),

		client.getLeaderboard(LeaderboardType.CREATORS, 200)
			.collectList()
			.map(list -> list.get(149).getCreatorPoints())
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Creators ranking 150th user's cp", o)),

		client.getFriends()
			.collectList()
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("My friend list", o)),

		client.getBlockedUsers()
			.collectList()
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Here are some bad guys", o)),

		client.blockUser(12109603)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Go away!", "User blocked!")),

		client.unblockUser(12109603)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("I'll forgive you..", "User unblocked!")),

		client.rateStars(62152040, 10, "jdash-client")
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Rate Ocular Miracle to 10*", "Rating sent!")),

		client.rateDemon(52374843, DemonDifficulty.EXTREME)
			.doOnError(Throwable::printStackTrace)
			.doOnSuccess(o -> printResult("Rate Zodiac to Extreme demon", "Rating sent!"))

		).block();
		
		System.out.println("End program");
	}
	
	private static void printResult(String title, Object obj) {
		System.out.println("------- " + title + " -------\n\t" + obj + "\n");
	}
}
