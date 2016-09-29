package androidluckyguys.raftingtrackinginhimachal.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidluckyguys.raftingtrackinginhimachal.R;
import androidluckyguys.raftingtrackinginhimachal.adapter.AlbumsAdapter;
import androidluckyguys.raftingtrackinginhimachal.model.Album;

import static androidluckyguys.raftingtrackinginhimachal.activity.SettingsActivity.EXTRA_CURRENT_ALBUM_POSITION;
import static androidluckyguys.raftingtrackinginhimachal.activity.SettingsActivity.EXTRA_STARTING_ALBUM_POSITION;
import static androidluckyguys.raftingtrackinginhimachal.common.Constants.ALBUM_IMAGE_URLS;
import static androidluckyguys.raftingtrackinginhimachal.common.Constants.ALBUM_NAMES;

public class CardViewActivity extends AppCompatActivity {
    private Drawer.Result drawerResult = null;

    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    private List<Album> albumList;
    private Bundle mTmpReenterState;
    private boolean mIsDetailsActivityStarted;

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mTmpReenterState != null) {
                int startingPosition = mTmpReenterState.getInt(EXTRA_STARTING_ALBUM_POSITION);
                int currentPosition = mTmpReenterState.getInt(EXTRA_CURRENT_ALBUM_POSITION);
                if (startingPosition != currentPosition) {
                    // If startingPosition != currentPosition the user must have swiped to a
                    // different page in the DetailsActivity. We must update the shared element
                    // so that the correct one falls into place.
                    String newTransitionName = ALBUM_NAMES[currentPosition];
                    View newSharedElement = recyclerView.findViewWithTag(newTransitionName);
                    if (newSharedElement != null) {
                        names.clear();
                        names.add(newTransitionName);
                        sharedElements.clear();
                        sharedElements.put(newTransitionName, newSharedElement);
                    }
                }

                mTmpReenterState = null;
            } else {
                // If mTmpReenterState is null, then the activity is exiting.
                View navigationBar = findViewById(android.R.id.navigationBarBackground);
                View statusBar = findViewById(android.R.id.statusBarBackground);
                if (navigationBar != null) {
                    names.add(navigationBar.getTransitionName());
                    sharedElements.put(navigationBar.getTransitionName(), navigationBar);
                }
                if (statusBar != null) {
                    names.add(statusBar.getTransitionName());
                    sharedElements.put(statusBar.getTransitionName(), statusBar);
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainview);

        setExitSharedElementCallback(mCallback);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();
*/
        activateDrawer();
        setUpRecylerView();


      //  prepareAlbums();

        try {
            //Glide.with(this).load(R.drawable.pic7).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        // Закрываем Navigation Drawer по нажатию системной кнопки "Назад" если он открыт
        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
    private void activateDrawer() {
        // Инициализируем Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initCollapsingToolbar();

        // Инициализируем Navigation Drawer
        drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_free_play),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom),
                        new SectionDrawerItem().withName(R.string.drawer_item_settings),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact)/*.withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(1)*/
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        InputMethodManager inputMethodManager = (InputMethodManager) CardViewActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(CardViewActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            Toast.makeText(CardViewActivity.this, CardViewActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                            if(CardViewActivity.this.getString(((Nameable) drawerItem).getNameRes()).equals("Adventure Sports"))
                            {
                                startActivity(new Intent(CardViewActivity.this, AdventureSportActivity.class));

                            }
                        }
                        if (drawerItem instanceof Badgeable) {
                            Badgeable badgeable = (Badgeable) drawerItem;
                            if (badgeable.getBadge() != null) {
                                // учтите, не делайте так, если ваш бейдж содержит символ "+"
                                try {
                                    int badge = Integer.valueOf(badgeable.getBadge());
                                    if (badge > 0) {
                                        drawerResult.updateBadge(String.valueOf(badge - 1), position);
                                    }
                                } catch (Exception e) {
                                    Log.d("test", "Не нажимайте на бейдж, содержащий плюс! :)");
                                }
                            }
                        }
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    // Обработка длинного клика, например, только для SecondaryDrawerItem
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SecondaryDrawerItem) {
                            Toast.makeText(CardViewActivity.this, CardViewActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })
                .build();
    }

    private void setUpRecylerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //albumList = new ArrayList<>();
        // adapter = new AlbumsAdapter(this, albumList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new CardAdapter() );
    }

    public void onResume() {
        super.onResume();
        mIsDetailsActivityStarted = false;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(CardViewActivity.this, SettingsActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /**
     * Adding few albums for testing
     */
    private void prepareAlbums() {
        int[] covers = new int[]{
                R.drawable.sking1,
                R.drawable.sking2,
                R.drawable.rafting1,
                R.drawable.rafting2,
                R.drawable.raft3,
                R.drawable.paragliding,
                R.drawable.para2,
                R.drawable.para4,
                R.drawable.zorbling_manali,
                R.drawable.trekking,

                R.drawable.rockclimbin,
                R.drawable.raft3,
                R.drawable.pic3,
                R.drawable.pic4,
                R.drawable.pic5,
                R.drawable.pic6,
                R.drawable.train,
                R.drawable.pic8,
                R.drawable.pic9,
                R.drawable.pic10,
                R.drawable.pic12};

        Album
        a = new Album("Sking", 13, covers[0]);       albumList.add(a);
        a = new Album("", 8, covers[1]);       albumList.add(a);
        a = new Album("Rafting", 11, covers[2]);        albumList.add(a);
        a = new Album("", 12, covers[3]);        albumList.add(a);
        a = new Album("", 14, covers[4]);        albumList.add(a);
        a = new Album("Paragling", 1, covers[5]);        albumList.add(a);
        a = new Album("", 11, covers[6]);        albumList.add(a);
        a = new Album("", 14, covers[7]);        albumList.add(a);
        a = new Album("Zorbling", 11, covers[8]);        albumList.add(a);
        a = new Album("Trekking", 17, covers[9]);        albumList.add(a);

        a = new Album("", 13, covers[10]);       albumList.add(a);
        a = new Album("", 8, covers[11]);       albumList.add(a);
        a = new Album("", 11, covers[12]);        albumList.add(a);
        a = new Album("", 12, covers[13]);        albumList.add(a);
        a = new Album("", 14, covers[14]);        albumList.add(a);
        a = new Album("", 1, covers[15]);        albumList.add(a);
        a = new Album("", 11, covers[16]);        albumList.add(a);
        a = new Album("", 14, covers[17]);        albumList.add(a);
        a = new Album("", 11, covers[18]);        albumList.add(a);
        a = new Album("", 17, covers[19]);        albumList.add(a);


        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onActivityReenter(int requestCode, Intent data) {
        super.onActivityReenter(requestCode, data);
        mTmpReenterState = new Bundle(data.getExtras());
        int startingPosition = mTmpReenterState.getInt(EXTRA_STARTING_ALBUM_POSITION);
        int currentPosition = mTmpReenterState.getInt(EXTRA_CURRENT_ALBUM_POSITION);
        if (startingPosition != currentPosition) {
            recyclerView.scrollToPosition(currentPosition);
        }
        postponeEnterTransition();
        recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                // TODO: figure out why it is necessary to request layout here in order to get a smooth transition.
                recyclerView.requestLayout();
                startPostponedEnterTransition();
                return true;
            }
        });
    }

    private class CardAdapter extends RecyclerView.Adapter<CardHolder> {
        private final LayoutInflater mInflater;

        public CardAdapter() {
            mInflater = LayoutInflater.from(CardViewActivity.this);
        }

        @Override
        public CardHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new CardHolder(mInflater.inflate(R.layout.album_card, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(CardHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return ALBUM_IMAGE_URLS.length;
        }
    }

    private class CardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mAlbumImage;
        TextView title;
        private int mAlbumPosition;

        public CardHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mAlbumImage = (ImageView) itemView.findViewById(R.id.thumbnail);
            mAlbumImage.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.title);
        }

        public void bind(int position) {
            Picasso.with(CardViewActivity.this).load(ALBUM_IMAGE_URLS[position]).into(mAlbumImage);
            mAlbumImage.setTransitionName(ALBUM_NAMES[position]);
            mAlbumImage.setTag(ALBUM_NAMES[position]);
            mAlbumPosition = position;

            title.setText(ALBUM_NAMES[position]);
        }

        @Override
        public void onClick(View v) {
            // TODO: is there a way to prevent user from double clicking and starting activity twice?
            Intent intent = new Intent(CardViewActivity.this, DetailsActivity.class);
            intent.putExtra(EXTRA_STARTING_ALBUM_POSITION, mAlbumPosition);

            if (!mIsDetailsActivityStarted) {
                mIsDetailsActivityStarted = true;
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(CardViewActivity.this,
                        mAlbumImage, mAlbumImage.getTransitionName()).toBundle());
            }
        }
    }

}

