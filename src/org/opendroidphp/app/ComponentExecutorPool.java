/*
 * *
 *  * This file is part of DroidPHP
 *  *
 *  * (c) 2014 Shushant Kumar
 *  *
 *  * For the full copyright and license information, please view the LICENSE
 *  * file that was distributed with this source code.
 *
 */

package org.opendroidphp.app;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shushant on 3/19/14.
 */
public class ComponentExecutorPool {

    //    private static List<Class<? extends ComponentProviderInterface>> executorClass;
    private static List<Class<?>> executorClass = new ArrayList<Class<?>>();
    private static boolean isConnected = false;

    private void ComponentProviderInterface() {
        //executorClass = new ArrayList<String>();

    }


    /**
     * Registers a components provider
     *
     * @param execClass A ComponentProviderInterface instance
     */
    public static void registerExecutor(Class<?> execClass) {

        executorClass.add(execClass);
        Log.d("Canonical class name:", execClass.getCanonicalName());

    }

//    public static void registerExecutor(List<ComponentProviderInterface> executorClassList) {
//
//        executorClass.addAll(executorClassList);
//
//    }
//
//    public static void registerExecutor(ComponentProviderInterface executor) {
//
//        executorClass.add(executor);
//
//    }

    /**
     * Connect all components provider
     * This method invokes the connect method of {@link ComponentProviderInterface}
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */

    public static void connectAll() throws IllegalAccessException, InstantiationException {

        if (isConnected) {
            return;
        }
        for (Class<?> component : executorClass) {

            Log.d("component", component.getCanonicalName());

            try {
                ((ComponentProviderInterface) component.newInstance()).connect();

            } catch (Exception e) {

                e.printStackTrace();
            }

        }
        isConnected = true;
//        for (ListIterator<?>
//                     connector = executorClass.listIterator(
//                executorClass.size());
//
//             connector.hasNext(); ) {
//
//            Log.d("instance", connector.next().getClass().getCanonicalName());
//                //((ComponentProviderInterface) connector.next().newInstance()).connect();
//
//        }
    }

    /**
     * Destroy all components provider
     * This method invokes the destroy method of {@link ComponentProviderInterface}
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */

    public static void destroyAll() throws IllegalAccessException, InstantiationException {

        if (!isConnected) {
            return;
        }

        for (Class<?> component : executorClass) {

            Log.d("component", component.getCanonicalName());

            try {
                ((ComponentProviderInterface) component.newInstance()).destroy();

            } catch (Exception e) {

                e.printStackTrace();
            }

        }
        isConnected = false;

//        for (ListIterator<Class<? extends ComponentProviderInterface>>
//                     connector = executorClass.listIterator(
//                executorClass.size());
//
//             connector.hasNext(); ) {
//
//            ((ComponentProviderInterface) connector.next().newInstance()).destroy();
//
//        }
    }

}
