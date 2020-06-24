package br.com.robsonldo.myutils.view;


import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.robsonldo.myutils.view.interfaces.OnBaseAdapterAction;

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    public final static int RECYCLER_ORIENTATION_HORIZONTAL = 1;
    public final static int RECYCLER_ORIENTATION_VERTICAL = 2;
    public final static int RECYCLER_ORIENTATION_GRID = 3;

    protected List<T> objects;
    protected List<T> objectsFull;

    protected OnBaseAdapterAction<T> onBaseAdapterAction;

    public BaseRecyclerViewAdapter() {

    }

    public BaseRecyclerViewAdapter(
            @NonNull List<T> objects,
            @NonNull OnBaseAdapterAction<T> onBaseAdapterAction
    ) {
        this.objects = objects;
        setObjectsFull(this.objects);
        this.onBaseAdapterAction = onBaseAdapterAction;
    }

    protected void setObjectsFull(@NonNull List<T> objects) {
        if(objectsFull != null) objectsFull.clear();
        else objectsFull = new ArrayList<>();

        objectsFull.addAll(objects);
    }

    @NonNull
    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position);

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        onVerifyEmpty();
    }

    public synchronized void set(List<T> objects) {
        this.objects = objects;
        setObjectsFull(this.objects);
        notifyDataSetChanged();

        onVerifyEmpty();
    }

    public synchronized void add(@NonNull T t) {
        if(objects == null) objects = new ArrayList<>();

        objects.add(t);
        setObjectsFull(objects);
        notifyItemInserted(objects.size() - 1);
        onVerifyEmpty();
    }

    public synchronized void addAll(@NonNull List<T> objects) {
        if (objects.isEmpty()) return;
        if(this.objects == null) this.objects = new ArrayList<>();

        int start = this.objects.isEmpty() ? 0 : this.objects.size();

        this.objects.addAll(objects);
        setObjectsFull(this.objects);
        notifyItemRangeInserted(start, objects.size());
        onVerifyEmpty();
    }

    public void updateElement(@NonNull T t) {
        int indexOf = getIndexObject(t);
        if (indexOf >= 0) updateElement(indexOf, t);
    }

    public void updateElement(int index) {
        updateElement(index, objects.get(index));
    }

    public synchronized void updateElement(int index, T object) {
        if (!validIndex(index)) return;

        objects.set(index, object);
        setObjectsFull(objects);

        notifyItemChanged(index);
    }

    public void removeElement(@NonNull T t) {
        int indexOf = getIndexObject(t);
        if (indexOf >= 0) removeElement(indexOf);
    }

    public synchronized void removeElement(int... indexes) {
        if (indexes.length == 0) return;

        if (indexes.length == 1) {
            removeElement(indexes[0]);
            return;
        }

        int small = Integer.MAX_VALUE;

        for (int index : indexes) {
            if (small > index) small = index;
            get().remove(index);
        }

        setObjectsFull(objects);
        notifyItemRangeRemoved(small, indexes.length);
        onVerifyEmpty();
    }

    public synchronized void removeElement(int index) {
        if (!validIndex(index)) return;

        objects.remove(index);
        setObjectsFull(objects);

        notifyItemRemoved(index);
        notifyItemRangeChanged(index, objects.size());
        onVerifyEmpty();
    }

    public void clearAll() {
        this.objects.clear();
        setObjectsFull(objects);

        notifyDataSetChanged();
        onVerifyEmpty();
    }

    public int getIndexObject(T t) {
        return get().indexOf(t);
    }

    private void onVerifyEmpty() {
        if (onBaseAdapterAction != null) onBaseAdapterAction.onEmpty(objects.isEmpty());
    }

    @NonNull
    public List<T> get() {
        if (objects == null) objects = new ArrayList<>();
        return objects;
    }

    @Nullable
    public T get(int index) {
        if (!validIndex(index)) return null;
        return get().get(index);
    }

    private boolean validIndex(int index) {
        return !get().isEmpty() && get().size() >= index + 1;
    }

    @Override
    public int getItemCount() {
        return objects != null ? objects.size() : 0;
    }

    public OnBaseAdapterAction<T> getOnBaseAdapterAction() {
        return onBaseAdapterAction;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence item) {
                List<T> filteredList = new ArrayList<>();

                if (item == null || item.length() == 0) {
                    filteredList.addAll(objectsFull);
                } else {
                    for (T t : objectsFull) {
                        if (isFilteringCondition(t, item.toString())) {
                            filteredList.add(t);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                results.count = filteredList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                objects.clear();
                objects.addAll((ArrayList<T>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    public Boolean isFilteringCondition(@Nullable T t, @NonNull String filter) {
        return t != null && t.toString().toLowerCase().contains(filter.trim().toLowerCase());
    }

    public abstract class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {
        public BaseRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract View getForegroundSwipe();
    }
}