package nucleus.presenter.delivery;

import rx.Notification;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public class DeliverFirst<View, T> implements Observable.Transformer<T, Delivery<View, T>> {

    private final Observable<View> view;

    public DeliverFirst(Observable<View> view) {
        this.view = view;
    }

    @Override
    public Observable<Delivery<View, T>> call(Observable<T> observable) {
        return Observable
            .combineLatest(
                view,
                observable.materialize().take(1),
                new Func2<View, Notification<T>, Delivery<View, T>>() {
                    @Override
                    public Delivery<View, T> call(View view, Notification<T> notification) {
                        return view == null ? null : new Delivery<>(view, notification);
                    }
                })
            .filter(new Func1<Delivery<View, T>, Boolean>() {
                @Override
                public Boolean call(Delivery<View, T> delivery) {
                    return delivery != null;
                }
            })
            .take(1);
    }
}
