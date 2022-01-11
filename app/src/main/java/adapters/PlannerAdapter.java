package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.foodborne.PlannerActivity;
import com.example.foodborne.R;
import com.squareup.picasso.Picasso;

import database.SQLiteHelper;
import models.APIUtils;
import models.Recipe;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class PlannerAdapter extends
        RecyclerView.Adapter<PlannerAdapter.ViewHolder> {

    List<Recipe> recetas;
    OnNoteListener mOnNoteListenerNull;
    OnNoteListener mOnNoteListenerImage;
    PlannerActivity plannerActivity;

    public PlannerAdapter(List<Recipe> recetas, OnNoteListener onNoteListenerNull, OnNoteListener onNoteListenerImage, PlannerActivity plannerActivity) throws IOException {
        assert(recetas.size() == 4) ;
        this.recetas = recetas;
        this.mOnNoteListenerNull = onNoteListenerNull;
        this.mOnNoteListenerImage = onNoteListenerImage;
        this.plannerActivity = plannerActivity;
    }

    @NonNull
    @Override
    public PlannerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View padre = (View) parent.getParent();
        TextView dateView = (TextView) padre.findViewById(R.id.txtDate);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.recycler_view_item_1, parent, false);
        // Return a new holder instance
        String date = dateView.getText().toString();
        PlannerAdapter.ViewHolder viewHolder = new ViewHolder(contactView, mOnNoteListenerNull, mOnNoteListenerImage, date);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlannerAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Recipe recipe = recetas.get(position);
        String name, image;
        if(recipe == null){
            name = "";
            image = "";
        } else {
            name = recipe.getTitle();
            image = /*"https://spoonacular.com/recipeImages/" + */ recipe.getImage();
        }

        // Set item views based on your views and data model
        TextView txtMeal = holder.txtMeal;
        String meal;
        switch (position){
            case 0: meal = "Breakfast";
                            break;
            case 1: meal = "Lunch";
                            break;
            case 2: meal = "Snack";
                            break;
            default: meal = "Dinner";
        }
        txtMeal.setText(meal);
        TextView recipeTitle = holder.recipeTitle;

        recipeTitle.setText(name);
        ImageView recipeImage = holder.recipeImage;
        //Picasso.get().load(image).into(recipeImage);
        Glide.with(holder.itemView).load(image).into(recipeImage);

        Button btnDel = holder.btnDel;
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteHelper sql = new SQLiteHelper(view.getContext());
                sql.deleteRecipeFromDay(holder.getLayoutPosition(), holder.date);
                plannerActivity.actualizarRecycler(holder.date);

            }
        });
    }

    @Override
    public int getItemCount() {
        return recetas.size();
    }

    public int getRecetasId(int position) {
        return recetas.get(position).getId();
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }




















    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView recipeImage;
        public TextView txtMeal;
        public TextView recipeTitle;
        private String date;

        public Button btnDel;

        OnNoteListener onNoteListenerNull;
        OnNoteListener onNoteListenerImage;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, OnNoteListener onNoteListenerNull, OnNoteListener onNoteListenerImage, String date) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            this.date = date;
            txtMeal = (TextView) itemView.findViewById(R.id.txtMeal);
            recipeTitle = (TextView) itemView.findViewById(R.id.recipeTitle);
            recipeImage = (ImageView) itemView.findViewById(R.id.recipeImage);

            btnDel = (Button) itemView.findViewById(R.id.btnDel);

            this.onNoteListenerNull = onNoteListenerNull;
            this.onNoteListenerImage = onNoteListenerImage;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int i = getAdapterPosition();
            if(recipeTitle.getText().toString() == ""){
                onNoteListenerNull.onNoteClick(i);
            } else {
                onNoteListenerImage.onNoteClick(i);
            }

        }
    }

}