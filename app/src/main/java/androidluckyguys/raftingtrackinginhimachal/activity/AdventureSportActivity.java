package androidluckyguys.raftingtrackinginhimachal.activity;

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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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

public class AdventureSportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    private List<Album> albumList;
    private RecyclerView mRecyclerView;
    private Bundle mTmpReenterState;
    private boolean mIsDetailsActivityStarted;

   /* private final SharedElementCallback mCallback = new SharedElementCallback() {
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
                    View newSharedElement = mRecyclerView.findViewWithTag(newTransitionName);
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
    };*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        albumList = new ArrayList<>();
        adapter = new AlbumsAdapter(this, albumList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

          prepareAlbums();

        try {
          //  Glide.with(this).load(R.drawable.pic7).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            startActivity(new Intent(AdventureSportActivity.this, SettingsActivity.class));

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
        //albumList = new ArrayList<>();
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
            mRecyclerView.scrollToPosition(currentPosition);
        }
        postponeEnterTransition();
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                // TODO: figure out why it is necessary to request layout here in order to get a smooth transition.
                mRecyclerView.requestLayout();
                startPostponedEnterTransition();
                return true;
            }
        });
    }

   /* private class CardAdapter extends RecyclerView.Adapter<CardHolder> {
        private final LayoutInflater mInflater;

        public CardAdapter() {
            mInflater = LayoutInflater.from(AdventureSportActivity.this);
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
        private int mAlbumPosition;

        public CardHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mAlbumImage = (ImageView) itemView.findViewById(R.id.thumbnail);
        }

        public void bind(int position) {
            Picasso.with(AdventureSportActivity.this).load(ALBUM_IMAGE_URLS[position]).into(mAlbumImage);
            mAlbumImage.setTransitionName(ALBUM_NAMES[position]);
            mAlbumImage.setTag(ALBUM_NAMES[position]);
            mAlbumPosition = position;
        }

        @Override
        public void onClick(View v) {
            // TODO: is there a way to prevent user from double clicking and starting activity twice?
            Intent intent = new Intent(AdventureSportActivity.this, DetailsActivity.class);
            intent.putExtra(EXTRA_STARTING_ALBUM_POSITION, mAlbumPosition);

            if (!mIsDetailsActivityStarted) {
                mIsDetailsActivityStarted = true;
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(AdventureSportActivity.this,
                        mAlbumImage, mAlbumImage.getTransitionName()).toBundle());
            }
        }
    }*/

}

