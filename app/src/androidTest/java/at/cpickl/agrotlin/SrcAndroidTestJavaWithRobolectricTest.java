package at.cpickl.agrotlin.app;


import android.app.Activity;
import android.widget.Button;

import com.google.inject.AbstractModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import at.cpickl.agrotlin.R;
import at.cpickl.agrotlin.activity.MainActivity;
import at.cpickl.agrotlin.service.SoundPlayer;
import roboguice.RoboGuice;

// https://github.com/roboguice/roboguice/wiki/Your-First-Testcase
@RunWith(RobolectricTestRunner.class)
public class SrcAndroidtestJavaWithRobolectricTest {
    // soundPlayer: SoundPlayer
    private SoundPlayer mockedSoundPlayer;

    @Before
    public void setup() {
        mockedSoundPlayer = Mockito.mock(SoundPlayer.class);
        // Override the default RoboGuice module
        RoboGuice.overrideApplicationInjector(Robolectric.application, new MyTestModule());
    }

    @After
    public void teardown() {
        // Don't forget to tear down our custom injector to avoid polluting other test classes
        RoboGuice.Util.reset();
    }

    @Test
    public void clickingButton_shouldChangeResultsViewText() throws Exception {
//        Activity activity = Robolectric.buildActivity(MainActivity.class).create().start();
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        Button pressMeButton = (Button) activity.findViewById(R.id.btnDebug);
//        TextView results = (TextView) activity.findViewById(R.id.results_text_view);

//        pressMeButton.performClick();
        Mockito.verifyNoMoreInteractions(mockedSoundPlayer);
//        String resultsText = results.getText().toString();
//        assertThat(resultsText, equalTo("Testing Android Rocks!"));
    }

    public class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(SoundPlayer.class).toInstance(mockedSoundPlayer);
        }
    }

}