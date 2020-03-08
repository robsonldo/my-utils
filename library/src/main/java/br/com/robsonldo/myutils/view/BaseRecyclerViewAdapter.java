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

    private List<T> objectsFilter;
    private List<T> objects;

    private OnBaseAdapterAction<T> onBaseAdapterAction;

    public BaseRecyclerViewAdapter(@NonNull List<T> objectsFilter,
                                   @NonNull OnBaseAdapterAction<T> onBaseAdapterAction) {

        this.objectsFilter = objectsFilter;
        this.objects = objectsFilter;
        this.onBaseAdapterAction = onBaseAdapterAction;
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
        this.objectsFilter = objects;
        notifyDataSetChanged();

        onVerifyEmpty();
    }

    public synchronized void add(@NonNull T t) {
        if(this.objectsFilter == null) this.objectsFilter = new ArrayList<>();

        this.objectsFilter.add(t);
        notifyItemInserted(this.objectsFilter.size() - 1);
        onVerifyEmpty();
    }

    public synchronized void addAll(@NonNull List<T> objects) {
        if(this.objectsFilter == null) this.objectsFilter = new ArrayList<>();

        this.objectsFilter.addAll(objects);
        notifyItemInserted(this.objectsFilter.size() - 1);
        onVerifyEmpty();
    }

    public void updateElement(@NonNull T t) {
        int indexOf = getIndexObject(t);
        if (indexOf >= 0) updateElement(indexOf, t);
    }

    public void updateElement(int index) {
        updateElement(index, objectsFilter.get(index));
    }

    public synchronized void updateElement(int index, T object) {
        if (!validIndex(index)) return;

        objectsFilter.set(index, object);
        notifyItemChanged(index);
    }

    public void removeElement(@NonNull T t) {
        int indexOf = getIndexObject(t);
        if (indexOf >= 0) removeElement(indexOf);
    }

    public synchronized void removeElement(int index) {
        if (!validIndex(index)) return;

        objectsFilter.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, objectsFilter.size());

        onVerifyEmpty();
    }

    public void clearAll() {
        this.objectsFilter = new ArrayList<>();
        notifyDataSetChanged();

        onVerifyEmpty();
    }

    public int getIndexObject(T t) {
        return get().indexOf(t);
    }

    private void onVerifyEmpty() {
        if (onBaseAdapterAction != null) onBaseAdapterAction.onEmpty(objectsFilter.isEmpty());
    }

    @NonNull
    public List<T> get() {
        if (objectsFilter == null) objectsFilter = new ArrayList<>();
        return objectsFilter;
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
        return objectsFilter != null ? objectsFilter.size() : 0;
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
                String filterString = item.toString().toLowerCase();
                FilterResults results = new FilterResults();

                final List<T> filtered = objects;
                int count = filtered.size();
                final ArrayList<T> newList = new ArrayList<>(count);

                for (int i = 0; i < count; i++) {
                    String filterableString = filtered.get(i).toString();
                    if (filterableString.toLowerCase().contains(filterString)) {
                        newList.add(filtered.get(i));
                    }
                }

                results.values = newList;
                results.count = newList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                objectsFilter = (ArrayList<T>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public abstract class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {
        public BaseRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract View foregroundSwipe();
    }
}