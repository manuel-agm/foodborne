package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodborne.R;

import models.Recipe;

import java.io.IOException;
import java.util.List;

//ADAPTADOR DE RECETAS
public class RecipesAdapter extends
        RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    List<Recipe> recetas;
    OnNoteListener mOnNoteListener;

    public RecipesAdapter(List<Recipe> recetas, OnNoteListener onNoteListener) throws IOException {
        if(recetas == null) throw new IOException("No se han encontrado resultados");
        this.recetas = recetas;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public RecipesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.recycler_view_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView, mOnNoteListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesAdapter.ViewHolder holder, int position) {
        Recipe recipe = recetas.get(position);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textName;
        public TextView textDesc;
        public ImageView imageRecipe;
        OnNoteListener onNoteListener;

        public ViewHolder(View itemView, OnNoteListener onNoteListener) {
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