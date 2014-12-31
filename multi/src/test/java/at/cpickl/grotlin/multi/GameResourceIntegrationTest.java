package at.cpickl.grotlin.multi;


import at.cpickl.grotlin.Player;
import at.cpickl.grotlin.Region;
import at.cpickl.grotlin.Simple4RegionsMap;
import at.cpickl.grotlin.TestDice;
import at.cpickl.grotlin.channel.AttackNotificationRto;
import at.cpickl.grotlin.channel.GameStartsNotification;
import at.cpickl.grotlin.channel.WaitingGameNotification;
import at.cpickl.grotlin.multi.resource.AttackOrderRto;
import at.cpickl.grotlin.multi.resource.GameResource;
import at.cpickl.grotlin.multi.resource.WaitingRandomGameRto;
import at.cpickl.grotlin.multi.service.ChannelApiService;
import at.cpickl.grotlin.multi.service.RunningGameService;
import at.cpickl.grotlin.multi.service.User;
import at.cpickl.grotlin.multi.service.UserGame;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * Kotlin mock hack: https://devnet.jetbrains.com/thread/443551
 */
@Test(groups = {"IntegrationTest"})
public class GameResourceIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(GameResourceIntegrationTest.class);

    public void createRandomGameForTwoUsersShouldStartGame() {
        final RunningGameService mockedRunningService = Mockito.mock(RunningGameService.class);
        final ChannelApiService mockedChannelApiService = Mockito.mock(ChannelApiService.class);

        Injector injector = Guice.createInjector(Modules.override(new AppModule()).with(new AbstractModule() {
            public void configure() {
                bind(RunningGameService.class).toInstance(mockedRunningService);
                bind(ChannelApiService.class).toInstance(mockedChannelApiService);
            }
        }));
        GameResource testee = injector.getInstance(GameResource.class);

        testee.getExistingOrCreateNewRandomGame(TestData.USER1);
        Collection<WaitingRandomGameRto> waitingGames = testee.listWaitingRandomGames();
        assertThat(waitingGames, hasSize(1));
        WaitingRandomGameRto waitingGame = waitingGames.iterator().next();
        assertThat(waitingGame.getUsersMax(), equalTo(2));
        assertThat(waitingGame.getUsersWaiting(), equalTo(1));

        testee.getExistingOrCreateNewRandomGame(TestData.USER2);

        verify(mockedRunningService).addNewGame(any(UserGame.class));
        Mockito.verifyNoMoreInteractions(mockedRunningService);
        // channelApiService.sendNotification(WaitingGameNotification(firstGame.users.size + 1), firstGame.users)
        verify(mockedChannelApiService).sendNotification(new WaitingGameNotification(2), Arrays.asList(TestData.USER1, TestData.USER2));
        Mockito.verifyNoMoreInteractions(mockedChannelApiService);
    }

    public void attackRegion() {
        User attackerUser = TestData.USER1;
        User defenderUser = TestData.USER2;

        Simple4RegionsMap map = new Simple4RegionsMap();
        Region attackerRegion = map.getR1();
        Region defenderRegion = map.getR2();

        UserGame game = UserGame.OBJECT$.build("testGameId", map,
            Arrays.asList(attackerUser, defenderUser), TestDice.OBJECT$.byRolls(6, 6, 1));
        Player attackerPlayer = game.asPlayer(attackerUser);
        Player defenderPlayer = game.asPlayer(defenderUser);


        attackerRegion.ownedBy(attackerPlayer, 2);
        defenderRegion.ownedBy(defenderPlayer, 1);

        // ------------

        final ChannelApiService mockedChannelApiService = Mockito.mock(ChannelApiService.class);
        Injector injector = Guice.createInjector(Modules.override(new AppModule()).with(new AbstractModule() {
            public void configure() {
                bind(ChannelApiService.class).toInstance(mockedChannelApiService);
            }
        }));
        injector.getInstance(RunningGameService.class).addNewGame(game);

        // ------------

        GameResource testee = injector.getInstance(GameResource.class);

        AttackOrderRto attackOrder = AttackOrderRto.OBJECT$.build(attackerRegion.getId(), defenderRegion.getId());
        AttackNotificationRto notification = testee.attackRegion(game.getId(), attackOrder, attackerUser);
        assertThat(notification, equalTo(AttackNotificationRto.OBJECT$.build(
            attackerUser.getName(), attackerRegion.getId(), defenderRegion.getId(), 12, 1)));

        NotificationTestUtil.assertSentNotifications(mockedChannelApiService,
            new NotificationTestUtil.InstanceOfMatcher(GameStartsNotification.class),
            new NotificationTestUtil.AllOfMatcher(
                new NotificationTestUtil.EqualsMatcher(notification.toDomain()),
                new NotificationTestUtil.ReceiversMatcher(defenderUser)
            )
        );
    }

}
