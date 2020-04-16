package com.example.rpac_sports_events;

import android.os.IBinder;
import android.view.WindowManager;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.Root;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.rpac_sports_events.Fragment.Login;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.*;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Before running this test makesure, user is not signed in
 */
@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Test
    public void checkRegisterButtonDisplay() {
        FragmentScenario.launchInContainer(Login.class, null, R.style.AppTheme, null);
        onView(withId(R.id.register_button)).check(matches(isDisplayed()));
    }

    @Test
    public void checkLoginButtonDisplay() {
        FragmentScenario.launchInContainer(Login.class, null, R.style.AppTheme, null);
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));
    }

    @Test
    public void checkForgetPasswordButtonDisplay() {
        FragmentScenario.launchInContainer(Login.class, null, R.style.AppTheme, null);
        onView(withId(R.id.forget_password_button)).check(matches(isDisplayed()));
    }

    @Test
    public void loginToRegisterNavigationTest() {
        TestNavHostController navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());
        navController.setGraph(R.navigation.nav_graph);

        FragmentScenario<Login> mlogin = FragmentScenario.launchInContainer(Login.class, null, R.style.AppTheme, null);
        mlogin.onFragment(fragment ->
                Navigation.setViewNavController(fragment.requireView(), navController));

        onView(withId(R.id.register_button)).perform(click());
        assertThat(navController.getCurrentDestination().getId(), is(R.id.register));
    }

    @Test
    public void loginToForgetPasswordNavigationTest() {
        TestNavHostController navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());
        navController.setGraph(R.navigation.nav_graph);

        FragmentScenario<Login> mlogin = FragmentScenario.launchInContainer(Login.class, null, R.style.AppTheme, null);
        mlogin.onFragment(fragment ->
                Navigation.setViewNavController(fragment.requireView(), navController));

        onView(withId(R.id.forget_password_button)).perform(click());
        assertThat(navController.getCurrentDestination().getId(), is(R.id.forget_password));

    }
}
