package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodborne.R;
import models.Recipe;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class RecipesAdapter extends
        RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    List<Recipe> recetas;
    OnNoteListener mOnNoteListener;

    public RecipesAdapter(List<Recipe> recetas, OnNoteListener onNoteListener){
        this.recetas = recetas;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public RecipesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.recycler_view_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView, mOnNoteListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Recipe recipe = recetas.get(position);

        // Set item views based on your views and data model
        TextView textName = holder.textName;
        textName.setText(recipe.getTitle());
        TextView textDesc = holder.textDesc;
        textDesc.setText("");
        ImageView imageRecipe = holder.imageRecipe;
        Glide.with(holder.itemView).load("https://spoonacular.com/recipeImages/" + recipe.getImage()).into(imageRecipe);
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
        public TextView textName;
        public TextView textDesc;
        public ImageView imageRecipe;

        OnNoteListener onNoteListener;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, OnNoteListener onNoteListener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            textName = (TextView) itemView.findViewById(R.id.textName);
            textDesc = (TextView) itemView.findViewById(R.id.textDesc);
            imageRecipe = (ImageView) itemView.findViewById(R.id.imgRecipe);

            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int i = getAdapterPosition();
            onNoteListener.onNoteClick(i);
        }
    }

}