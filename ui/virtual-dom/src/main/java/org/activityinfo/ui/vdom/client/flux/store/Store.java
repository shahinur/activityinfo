package org.activityinfo.ui.vdom.client.flux.store;

/**
 * Stores contain the application state and logic.
 *
 * <p>Their role is somewhat similar to a model in a traditional MVC, but they manage the state of many objects — they
 * are not instances of one object. Nor are they the same as Backbone's collections. More than simply managing a
 * collection of ORM-style objects, stores manage the application state for a particular domain within the application.
 *
 * <p>For example, Facebook's Lookback Video Editor utilized a TimeStore that kept track of the playback time position
 * and the playback state. On the other hand, the same application's ImageStore kept track of a collection of images.
 * The TodoStore in our TodoMVC example is similar in that it manages a collection of to-do items. A store exhibits
 * characteristics of both a collection of models and a singleton model of a logical domain.
 *
 * <p>As mentioned above, a store registers itself with the dispatcher and provides it with a callback. This callback
 * receives a data payload as a parameter. The payload contains an action with an attribute identifying the action's
 * type. Within the store's registered callback, a switch statement based on the action's type is used to interpret
 * the payload and to provide the proper hooks into the store's internal methods. This allows an action to result in
 * an update to the state of the store, via the dispatcher. After the stores are updated,
 * they broadcast an event declaring that their state has changed, so the views may query the new state and update
 * themselves.
 *
 *
 */
public interface Store {

    void addChangeListener(StoreChangeListener listener);

    void removeChangeListener(StoreChangeListener listener);

}
