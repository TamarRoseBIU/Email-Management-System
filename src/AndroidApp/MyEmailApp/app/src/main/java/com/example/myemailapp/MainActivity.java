package com.example.myemailapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDelegate;
import com.bumptech.glide.Glide;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myemailapp.fragments.LabelsFragment;
import com.example.myemailapp.fragments.SearchResultFragment;
import com.example.myemailapp.ui.login.LoginActivity;
import com.example.myemailapp.fragments.HomeFragment;
import com.example.myemailapp.fragments.InboxFragment;
import com.example.myemailapp.fragments.SentFragment;
import com.example.myemailapp.fragments.SpamFragment;
import com.example.myemailapp.fragments.StarredFragment;
import com.example.myemailapp.fragments.DraftsFragment;
import com.example.myemailapp.fragments.TrashFragment;
import com.example.myemailapp.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.myemailapp.fragments.ComposeMailFragment;
import com.google.android.material.search.SearchBar;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String BASE_URL = BuildConfig.BASE_URL_NO_API; // 10.0.2.2 = localhost from emulator
    //String baseUrl = BuildConfig.BASE_URL;
    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private String username = "User"; // Default username

    private String firstName = "User";
    private String currentFragment = "home";
    private EditText searchInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before calling super.onCreate()
        applyTheme();

        super.onCreate(savedInstanceState);

        try {

            setContentView(R.layout.activity_main);

            searchInput = findViewById(R.id.search_input);

            searchInput.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER && event.getAction() == android.view.KeyEvent.ACTION_DOWN)) {

                    String query = searchInput.getText().toString().trim();
                    if (!query.isEmpty()) {
                        openSearchResultFragment(query);

                        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                        }
                    }
                    return true;
                }
                return false;
            });


            // Get username from token/preferences
            getUsernameFromToken();
            getUserFromPrefs();
            setupToolbar();
            setupNavigationDrawer();

            NavigationView navView = findViewById(R.id.nav_view);
            navView.setItemIconTintList(null);
            View header = navView.getHeaderView(0);
            ImageView navImage = header.findViewById(R.id.nav_header_image);

            SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
            String profilePath = prefs.getString("profilePic", "");

            if (!profilePath.isEmpty()) {
                String fullUrl = BASE_URL + profilePath;

                Glide.with(this)
                        .load(fullUrl)
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .circleCrop()  // <--- This makes the image circular
                        .into(navImage);

            }

            // Load initial fragment (Home)
            if (savedInstanceState == null) {
                loadFragment(new HomeFragment(), getString(R.string.nav_home));
                currentFragment = "home";
            }

            // Update navigation header with username
            updateNavigationHeader();

            FloatingActionButton fabCompose = findViewById(R.id.fab_compose);
            fabCompose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    if (toolbar != null) toolbar.setVisibility(View.GONE);

                    FloatingActionButton fabCompose = findViewById(R.id.fab_compose);
                    if (fabCompose != null) fabCompose.setVisibility(View.GONE);


                    DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                    if (drawerLayout != null) drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                    ComposeMailFragment composeFragment = new ComposeMailFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, composeFragment)
                            .addToBackStack("compose_email")
                            .commit();
                }
            });

            /*fabCompose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ComposeMailFragment composeFragment = new ComposeMailFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, composeFragment)
                            .addToBackStack("compose_email")
                            .commit();
                }
            });*/

            getSupportFragmentManager().addOnBackStackChangedListener(() -> {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                Toolbar toolbar = findViewById(R.id.toolbar);
                //  FloatingActionButton fabCompose = findViewById(R.id.fab_compose);
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

                if (currentFragment instanceof ComposeMailFragment) {
                    if (toolbar != null) toolbar.setVisibility(View.GONE);
                    if (fabCompose != null) fabCompose.setVisibility(View.GONE);
                    if (drawerLayout != null) drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                } else {
                    // אנחנו מחוץ ל־ComposeMailFragment - הראה את ה־UI
                    if (toolbar != null) toolbar.setVisibility(View.VISIBLE);
                    if (fabCompose != null) fabCompose.setVisibility(View.VISIBLE);
                    if (drawerLayout != null) drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in MainActivity onCreate", e);
            createFallbackLayout();
        }
    }

    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences("theme", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void toggleTheme() {
        SharedPreferences prefs = getSharedPreferences("theme", MODE_PRIVATE);
        boolean currentDarkMode = prefs.getBoolean("dark_mode", false);
        boolean newDarkMode = !currentDarkMode;

        // Save the new theme preference
        prefs.edit().putBoolean("dark_mode", newDarkMode).apply();

        // Apply the new theme
        if (newDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Show a toast to indicate the theme change
//        String message = newDarkMode ? "Dark mode enabled" : "Light mode enabled";
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getString(R.string.nav_home));
            }
        }
    }

    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        if (drawerLayout != null && navigationView != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, findViewById(R.id.toolbar),
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            navigationView.setNavigationItemSelectedListener(this);

            // Set home as checked by default
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void getUsernameFromToken() {
        try {
            SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);

            // First try to get username directly from SharedPreferences
            String savedUsername = prefs.getString("username", null);
            if (savedUsername != null && !savedUsername.isEmpty()) {
                username = savedUsername;
                Log.d(TAG, "Username from SharedPreferences: " + username);
                return;
            }

            // Fallback: try to decode from JWT token
            String token = prefs.getString("jwt", null);
            if (token != null) {
                String[] tokenParts = token.split("\\.");
                if (tokenParts.length > 1) {
                    String payload = new String(Base64.decode(tokenParts[1], Base64.URL_SAFE));
                    JSONObject jsonPayload = new JSONObject(payload);
                    String userId = jsonPayload.optString("id", "");
                    Log.d(TAG, "User ID from token: " + userId);

                    // For now, use default username
                    username = "User";
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting username", e);
            username = "User";
        }
    }

    private void getUserFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String savedFirstName = prefs.getString("firstName", null);
        if (savedFirstName != null && !savedFirstName.isEmpty()) {
            firstName = savedFirstName;
        }
        String savedUserName = prefs.getString("username", null);
        if (savedUserName != null && !savedUserName.isEmpty()) {
            username = savedUserName;
        }

        // (Optional) fall back to username if you like
    }

    private void updateNavigationHeader() {
        if (navigationView == null) return;
        View header = navigationView.getHeaderView(0);
        if (header == null) return;

        TextView navFirstName = header.findViewById(R.id.nav_header_firstName);
        if (navFirstName != null) {
            String hello = getString(R.string.hello_user, firstName);
            navFirstName.setText(hello);
        }

        TextView navUserName = header.findViewById(R.id.nav_header_username);
        if (navUserName != null) {
            navUserName.setText(username);
        }
    }

    private void loadFragment(Fragment fragment, String title) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();

            // Update toolbar title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }

            Log.d(TAG, "Loaded fragment: " + title);
        } catch (Exception e) {
            Log.e(TAG, "Error loading fragment: " + title, e);
        }
    }

    private void createFallbackLayout() {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 100, 50, 50);
        layout.setGravity(android.view.Gravity.CENTER);

        TextView textView = new TextView(this);
        textView.setText(
                String.format(getString(R.string.hello_user), username)
        );

        textView.setTextSize(20);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setPadding(20, 50, 20, 20);

        layout.addView(textView);
        setContentView(layout);

        Log.d(TAG, "Using fallback layout with username: " + username);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_theme_toggle) {
            toggleTheme();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        String title = "";
        String fragmentTag = "";

        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
            title = getString(R.string.nav_home);
            fragmentTag = "home";
        }
        else if (id == R.id.nav_inbox) {
            fragment = new InboxFragment();
            title = getString(R.string.nav_inbox);
            fragmentTag = "inbox";
        }
        else if (id == R.id.nav_starred) {
            fragment = new StarredFragment();
            title = getString(R.string.nav_starred);
            fragmentTag = "starred";
        }
        else if (id == R.id.nav_sent) {
            fragment = new SentFragment();
            title = getString(R.string.nav_sent);
            fragmentTag = "sent";
        }
        else if (id == R.id.nav_drafts) {
            fragment = new DraftsFragment();
            title = getString(R.string.nav_drafts);
            fragmentTag = "drafts";
        }
        else if (id == R.id.nav_spam) {
            fragment = new SpamFragment();
            title = getString(R.string.nav_spam);
            fragmentTag = "spam";
        }
        else if (id == R.id.nav_trash) {
            fragment = new TrashFragment();
            title = getString(R.string.nav_trash);
            fragmentTag = "trash";
        }
        else if (id == R.id.nav_logout) {
            logout();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.viewLabels) {
            fragment = new LabelsFragment();
            title = getString(R.string.nav_view_labels);
            fragmentTag = "labels";
        } else if (id == R.id.nav_profile) {
            fragment = new ProfileFragment();
            title = getString(R.string.nav_profile);
            fragmentTag = "profile";
        }

        if (fragment != null ) {
            loadFragment(fragment, title);
            currentFragment = fragmentTag;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void logout() {
        // Clear token and username
        getSharedPreferences("auth", MODE_PRIVATE)
                .edit()
                .remove("jwt")
                .remove("username")
                .apply();

        // Go back to login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openSearchResultFragment(String query) {
        Bundle bundle = new Bundle();
        bundle.putString("search_type", "query");
        bundle.putString("search_term", query);

        SearchResultFragment searchFragment = new SearchResultFragment();
        searchFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, searchFragment)
                .addToBackStack("query_search")
                .commit();
    }

}