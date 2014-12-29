package at.cpickl.grotlin.multi;


import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;

import at.cpickl.grotlin.channel.WaitingGameNotification;
import at.cpickl.grotlin.multi.resource.GameResource;
import at.cpickl.grotlin.multi.resource.WaitingRandomGameRto;
import at.cpickl.grotlin.multi.service.ChannelApiService;
import at.cpickl.grotlin.multi.service.RunningGameService;
import at.cpickl.grotlin.multi.service.UserGame;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * Kotlin mock hack: https://devnet.jetbrains.com/thread/443551
 */
@Test(groups = {"IntegrationTest"})
public class GameResourceIntegrationTest {

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

        Mockito.verify(mockedRunningService).addNewGame(Matchers.any(UserGame.class));
        Mockito.verifyNoMoreInteractions(mockedRunningService);
        // channelApiService.sendNotification(WaitingGameNotification(firstGame.users.size + 1), firstGame.users)
        Mockito.verify(mockedChannelApiService).sendNotification(new WaitingGameNotification(2), Arrays.asList(TestData.USER1, TestData.USER2));
        Mockito.verifyNoMoreInteractions(mockedChannelApiService);
    }
}
