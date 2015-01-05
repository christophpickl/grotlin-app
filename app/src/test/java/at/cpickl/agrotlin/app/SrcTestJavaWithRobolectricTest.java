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
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import at.cpickl.agrotlin.R;
import at.cpickl.agrotlin.activity.MainActivity;
import at.cpickl.agrotlin.service.SoundPlayer;
import roboguice.RoboGuice;

// https://github.com/roboguice/roboguice/wiki/Your-First-Testcase
/*
my api level is 19/KITKAT.
supported ones:
    JELLY_BEAN is 16
    JELLY_BEAN_MR1 is 17
    JELLY_BEAN_MR2 is 18
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class SrcTestJavaWithRobolectricTest {
    // soundPlayer: SoundPlayer
    private SoundPlayer mockedSoundPlayer;

    @Before
    public void setup() {
        mockedSoundPlayer = Mockito.mock(SoundPlayer.class);
        // Override the default RoboGuice module
        System.out.println("TEST setup...");
        RoboGuice.overrideApplicationInjector(Robolectric.application, new MyTestModule());
        System.out.println("TEST setup... DONE");
    }

    @After
    public void teardown() {
        System.out.println("TEST teardown...");
        // Don't forget to tear down our custom injector to avoid polluting other test classes
        RoboGuice.Util.reset();
        System.out.println("TEST teardown... DONE");
    }

    @Test
    public void clickingButton_shouldChangeResultsViewText() throws Exception {
//        Activity activity = Robolectric.buildActivity(MainActivity.class).create().start();
        System.out.println("TEST START! create...");
        ActivityController<MainActivity> ctrl = Robolectric.buildActivity(MainActivity.class).create();
        System.out.println("TEST get...");
        Activity activity = ctrl.get();
        System.out.println("TEST button...");
        Button pressMeButton = (Button) activity.findViewById(R.id.btnDebug);
//        TextView results = (TextView) activity.findViewById(R.id.results_text_view);

//        pressMeButton.performClick();
//        Mockito.verifyNoMoreInteractions(mockedSoundPlayer);
//        String resultsText = results.getText().toString();
//        assertThat(resultsText, equalTo("Testing Android Rocks!"));
        System.out.println("TEST DONE!");
    }

    public class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(SoundPlayer.class).toInstance(mockedSoundPlayer);
        }
    }

}