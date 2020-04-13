package com.example.rpac_sports_events;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;

import com.example.rpac_sports_events.Fragment.Favorite;
import com.example.rpac_sports_events.Fragment.Login;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.is;

public class FavoriteTest {
    @Test
    public void checkLoginButtonDisplay() {
        FragmentScenario.launchInContainer(Favorite.class, null, R.style.AppTheme, null);
        onView(withId(R.id.favorite_login_button)).check(matches(isDisplayed()));
    }

    // This test requires animation to be turned off
    @Test
    public void FavoriteToLoginNavigationTest() {
        TestNavHostController navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());
        navController.setGraph(R.navigation.nav_graph);

        FragmentScenario<Favorite> mlogin = FragmentScenario.launchInContainer(Favorite.class, null, R.style.AppTheme, null);
        mlogin.onFragment(fragment ->
                Navigation.setViewNavController(fragment.requireView(), navController));

        onView(withId(R.id.favorite_login_button)).perform(click());
        assertThat(navController.getCurrentDestination().getId(), is(R.id.login));
    }
}
