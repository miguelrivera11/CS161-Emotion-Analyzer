
// Copyright 2016 The TensorFlow Authors. All Rights Reserved.
//Adpated by Miguel Rivera to accept tensors as input so that 2D tensors could be accepted

package com.example.smart.emotionanalyzer;

import android.content.res.AssetManager;

import android.os.Build.VERSION;

import android.os.Trace;

import android.text.TextUtils;

import android.util.Log;

import java.io.ByteArrayOutputStream;

import java.io.FileInputStream;

import java.io.IOException;

import java.io.InputStream;

import java.nio.ByteBuffer;

import java.nio.DoubleBuffer;

import java.nio.FloatBuffer;

import java.nio.IntBuffer;

import java.nio.LongBuffer;

import java.util.ArrayList;

import java.util.List;

import org.tensorflow.Graph;

import org.tensorflow.Operation;

import org.tensorflow.Session;

import org.tensorflow.Tensor;

import org.tensorflow.TensorFlow;

import org.tensorflow.Tensors;

import org.tensorflow.contrib.android.RunStats;
import org.tensorflow.types.UInt8;

public class AdaptedTensorFlowInferenceInterface {
    private static final String TAG = "TensorFlowInferenceInterface";

    private static final String ASSET_FILE_PREFIX = "file:///android_asset/";



    /*

     * Load a TensorFlow model from the AssetManager or from disk if it is not an asset file.

     *

     * @param assetManager The AssetManager to use to load the model file.

     * @param model The filepath to the GraphDef proto representing the model.

     */

    // Immutable state.

    private final String modelName;

    private final Graph g;

    private final Session sess;



    // State reset on every call to run.

    private Session.Runner runner;

    private List<String> feedNames = new ArrayList<String>();

    private List<Tensor<?>> feedTensors = new ArrayList<Tensor<?>>();

    private List<String> fetchNames = new ArrayList<String>();

    private List<Tensor<?>> fetchTensors = new ArrayList<Tensor<?>>();



    // Mutable state.

    private RunStats runStats;

    public AdaptedTensorFlowInferenceInterface(AssetManager assetManager, String model) {

        prepareNativeRuntime();



        this.modelName = model;

        this.g = new Graph();

        this.sess = new Session(g);

        this.runner = sess.runner();



        final boolean hasAssetPrefix = model.startsWith(ASSET_FILE_PREFIX);

        InputStream is = null;

        try {

            String aname = hasAssetPrefix ? model.split(ASSET_FILE_PREFIX)[1] : model;

            is = assetManager.open(aname);

        } catch (IOException e) {

            if (hasAssetPrefix) {

                throw new RuntimeException("Failed to load model from '" + model + "'", e);

            }

            // Perhaps the model file is not an asset but is on disk.

            try {

                is = new FileInputStream(model);

            } catch (IOException e2) {

                throw new RuntimeException("Failed to load model from '" + model + "'", e);

            }

        }



        try {

            if (VERSION.SDK_INT >= 18) {

                Trace.beginSection("initializeTensorFlow");

                Trace.beginSection("readGraphDef");

            }



            // TODO(ashankar): Can we somehow mmap the contents instead of copying them?

            byte[] graphDef = new byte[is.available()];

            final int numBytesRead = is.read(graphDef);

            if (numBytesRead != graphDef.length) {

                throw new IOException(

                        "read error: read only "

                                + numBytesRead

                                + " of the graph, expected to read "

                                + graphDef.length);

            }



            if (VERSION.SDK_INT >= 18) {

                Trace.endSection(); // readGraphDef.

            }



            loadGraph(graphDef, g);

            is.close();

            Log.i(TAG, "Successfully loaded model from '" + model + "'");



            if (VERSION.SDK_INT >= 18) {

                Trace.endSection(); // initializeTensorFlow.

            }

        } catch (IOException e) {

            throw new RuntimeException("Failed to load model from '" + model + "'", e);

        }

    }
    /**

     * Runs inference between the previously registered input nodes (via feed*) and the requested

     * output nodes. Output nodes can then be queried with the fetch* methods.

     *

     * @param outputNames A list of output nodes which should be filled by the inference pass.

     */

    public void run(String[] outputNames) {

        run(outputNames, false);

    }



    /**

     * Runs inference between the previously registered input nodes (via feed*) and the requested

     * output nodes. Output nodes can then be queried with the fetch* methods.

     *

     * @param outputNames A list of output nodes which should be filled by the inference pass.

     */

    public void run(String[] outputNames, boolean enableStats) {

        run(outputNames, enableStats, new String[] {});

    }



    /** An overloaded version of runInference that allows supplying targetNodeNames as well */

    public void run(String[] outputNames, boolean enableStats, String[] targetNodeNames) {

        // Release any Tensors from the previous run calls.

        closeFetches();



        // Add fetches.

        for (String o : outputNames) {

            fetchNames.add(o);

            TensorId tid = TensorId.parse(o);

            runner.fetch(tid.name, tid.outputIndex);

        }



        // Add targets.

        for (String t : targetNodeNames) {

            runner.addTarget(t);

        }



        // Run the session.

        try {

            if (enableStats) {

                Session.Run r = runner.setOptions(RunStats.runOptions()).runAndFetchMetadata();

                fetchTensors = r.outputs;



                if (runStats == null) {

                    runStats = new RunStats();

                }

                runStats.add(r.metadata);

            } else {

                fetchTensors = runner.run();

            }

        } catch (RuntimeException e) {

            // Ideally the exception would have been let through, but since this interface predates the

            // TensorFlow Java API, must return -1.

            Log.e(

                    TAG,

                    "Failed to run TensorFlow inference with inputs:["

                            + TextUtils.join(", ", feedNames)

                            + "], outputs:["

                            + TextUtils.join(", ", fetchNames)

                            + "]");

            throw e;

        } finally {

            // Always release the feeds (to save resources) and reset the runner, this run is

            // over.

            closeFeeds();

            runner = sess.runner();

        }

    }



    /** Returns a reference to the Graph describing the computation run during inference. */

    public Graph graph() {

        return g;

    }



    public Operation graphOperation(String operationName) {

        final Operation operation = g.operation(operationName);

        if (operation == null) {

            throw new RuntimeException(

                    "Node '" + operationName + "' does not exist in model '" + modelName + "'");

        }

        return operation;

    }



    /** Returns the last stat summary string if logging is enabled. */

    public String getStatString() {

        return (runStats == null) ? "" : runStats.summary();

    }



    /**

     * Cleans up the state associated with this Object.

     *

     * <p>The TenosrFlowInferenceInterface object is no longer usable after this method returns.

     */

    public void close() {

        closeFeeds();

        closeFetches();

        sess.close();

        g.close();

        if (runStats != null) {

            runStats.close();

        }

        runStats = null;

    }



    @Override

    protected void finalize() throws Throwable {

        try {

            close();

        } finally {

            super.finalize();

        }

    }



    // Methods for taking a native Tensor and filling it with values from Java arrays.



    /**

     * Given a source array with shape {@link dims} and content {@link src}, copy the contents into

     * the input Tensor with name {@link inputName}. The source array {@link src} must have at least

     * as many elements as that of the destination Tensor. If {@link src} has more elements than the

     * destination has capacity, the copy is truncated.

     */

    public void feed(String inputName, boolean[] src, long... dims) {

        byte[] b = new byte[src.length];



        for (int i = 0; i < src.length; i++) {

            b[i] = src[i] ? (byte) 1 : (byte) 0;

        }



        addFeed(inputName, Tensor.create(Boolean.class, dims, ByteBuffer.wrap(b)));

    }



    /**

     * Given a source array with shape {@link dims} and content {@link src}, copy the contents into

     * the input Tensor with name {@link inputName}. The source array {@link src} must have at least

     * as many elements as that of the destination Tensor. If {@link src} has more elements than the

     * destination has capacity, the copy is truncated.

     */

    public void feed(String inputName, float[] src, long... dims) {

        addFeed(inputName, Tensor.create(dims, FloatBuffer.wrap(src)));

    }



    /**

     * Given a source array with shape {@link dims} and content {@link src}, copy the contents into

     * the input Tensor with name {@link inputName}. The source array {@link src} must have at least

     * as many elements as that of the destination Tensor. If {@link src} has more elements than the

     * destination has capacity, the copy is truncated.

     */

    public void feed(String inputName, int[] src, long... dims) {

        addFeed(inputName, Tensor.create(dims, IntBuffer.wrap(src)));

    }



    /**

     * Given a source array with shape {@link dims} and content {@link src}, copy the contents into

     * the input Tensor with name {@link inputName}. The source array {@link src} must have at least

     * as many elements as that of the destination Tensor. If {@link src} has more elements than the

     * destination has capacity, the copy is truncated.

     */

    public void feed(String inputName, long[] src, long... dims) {

        addFeed(inputName, Tensor.create(dims, LongBuffer.wrap(src)));

    }



    /**

     * Given a source array with shape {@link dims} and content {@link src}, copy the contents into

     * the input Tensor with name {@link inputName}. The source array {@link src} must have at least

     * as many elements as that of the destination Tensor. If {@link src} has more elements than the

     * destination has capacity, the copy is truncated.

     */

    public void feed(String inputName, double[] src, long... dims) {

        addFeed(inputName, Tensor.create(dims, DoubleBuffer.wrap(src)));

    }



    /**

     * Given a source array with shape {@link dims} and content {@link src}, copy the contents into

     * the input Tensor with name {@link inputName}. The source array {@link src} must have at least

     * as many elements as that of the destination Tensor. If {@link src} has more elements than the

     * destination has capacity, the copy is truncated.

     */

    public void feed(String inputName, byte[] src, long... dims) {

        addFeed(inputName, Tensor.create(UInt8.class, dims, ByteBuffer.wrap(src)));

    }



    /**

     * Copy a byte sequence into the input Tensor with name {@link inputName} as a string-valued

     * scalar tensor. In the TensorFlow type system, a "string" is an arbitrary sequence of bytes, not

     * a Java {@code String} (which is a sequence of characters).

     */

    public void feedString(String inputName, byte[] src) {

        addFeed(inputName, Tensors.create(src));

    }



    /**

     * Copy an array of byte sequences into the input Tensor with name {@link inputName} as a

     * string-valued one-dimensional tensor (vector). In the TensorFlow type system, a "string" is an

     * arbitrary sequence of bytes, not a Java {@code String} (which is a sequence of characters).

     */

    public void feedString(String inputName, byte[][] src) {

        addFeed(inputName, Tensors.create(src));

    }



    // Methods for taking a native Tensor and filling it with src from Java native IO buffers.



    /**

     * Given a source buffer with shape {@link dims} and content {@link src}, both stored as

     * <b>direct</b> and <b>native ordered</b> java.nio buffers, copy the contents into the input

     * Tensor with name {@link inputName}. The source buffer {@link src} must have at least as many

     * elements as that of the destination Tensor. If {@link src} has more elements than the

     * destination has capacity, the copy is truncated.

     */

    public void feed(String inputName, FloatBuffer src, long... dims) {

        addFeed(inputName, Tensor.create(dims, src));

    }



    /**

     * Given a source buffer with shape {@link dims} and content {@link src}, both stored as

     * <b>direct</b> and <b>native ordered</b> java.nio buffers, copy the contents into the input

     * Tensor with name {@link inputName}. The source buffer {@link src} must have at least as many

     * elements as that of the destination Tensor. If {@link src} has more elements than the

     * destination has capacity, the copy is truncated.

     */

    public void feed(String inputName, IntBuffer src, long... dims) {

        addFeed(inputName, Tensor.create(dims, src));

    }



    /**

     * Given a source buffer with shape {@link dims} and content {@link src}, both stored as

     * <b>direct</b> and <b>native ordered</b> java.nio buffers, copy the contents into the input

     * Tensor with name {@link inputName}. The source buffer {@link src} must have at least as many

     * elements as that of the destination Tensor. If {@link src} has more elements than the

     * destination has capacity, the copy is truncated.

     */

    public void feed(String inputName, LongBuffer src, long... dims) {

        addFeed(inputName, Tensor.create(dims, src));

    }



    /**

     * Given a source buffer with shape {@link dims} and content {@link src}, both stored as

     * <b>direct</b> and <b>native ordered</b> java.nio buffers, copy the contents into the input

     * Tensor with name {@link inputName}. The source buffer {@link src} must have at least as many

     * elements as that of the destination Tensor. If {@link src} has more elements than the

     * destination has capacity, the copy is truncated.

     */

    public void feed(String inputName, DoubleBuffer src, long... dims) {

        addFeed(inputName, Tensor.create(dims, src));

    }

    public void feed(String inputName, Tensor<Integer> tensor) {
        addFeed(inputName, tensor);
    }



    /**

     * Given a source buffer with shape {@link dims} and content {@link src}, both stored as

     * <b>direct</b> and <b>native ordered</b> java.nio buffers, copy the contents into the input

     * Tensor with name {@link inputName}. The source buffer {@link src} must have at least as many

     * elements as that of the destination Tensor. If {@link src} has more elements than the

     * destination has capacity, the copy is truncated.

     */

    public void feed(String inputName, ByteBuffer src, long... dims) {

        addFeed(inputName, Tensor.create(UInt8.class, dims, src));

    }



    /**

     * Read from a Tensor named {@link outputName} and copy the contents into a Java array. {@link

     * dst} must have length greater than or equal to that of the source Tensor. This operation will

     * not affect dst's content past the source Tensor's size.

     */

    public void fetch(String outputName, float[] dst) {

        fetch(outputName, FloatBuffer.wrap(dst));

    }



    /**

     * Read from a Tensor named {@link outputName} and copy the contents into a Java array. {@link

     * dst} must have length greater than or equal to that of the source Tensor. This operation will

     * not affect dst's content past the source Tensor's size.

     */

    public void fetch(String outputName, int[] dst) {

        fetch(outputName, IntBuffer.wrap(dst));

    }



    /**

     * Read from a Tensor named {@link outputName} and copy the contents into a Java array. {@link

     * dst} must have length greater than or equal to that of the source Tensor. This operation will

     * not affect dst's content past the source Tensor's size.

     */

    public void fetch(String outputName, long[] dst) {

        fetch(outputName, LongBuffer.wrap(dst));

    }



    /**

     * Read from a Tensor named {@link outputName} and copy the contents into a Java array. {@link

     * dst} must have length greater than or equal to that of the source Tensor. This operation will

     * not affect dst's content past the source Tensor's size.

     */

    public void fetch(String outputName, double[] dst) {

        fetch(outputName, DoubleBuffer.wrap(dst));

    }



    /**

     * Read from a Tensor named {@link outputName} and copy the contents into a Java array. {@link

     * dst} must have length greater than or equal to that of the source Tensor. This operation will

     * not affect dst's content past the source Tensor's size.

     */

    public void fetch(String outputName, byte[] dst) {

        fetch(outputName, ByteBuffer.wrap(dst));

    }



    /**

     * Read from a Tensor named {@link outputName} and copy the contents into the <b>direct</b> and

     * <b>native ordered</b> java.nio buffer {@link dst}. {@link dst} must have capacity greater than

     * or equal to that of the source Tensor. This operation will not affect dst's content past the

     * source Tensor's size.

     */

    public void fetch(String outputName, FloatBuffer dst) {

        getTensor(outputName).writeTo(dst);

    }



    /**

     * Read from a Tensor named {@link outputName} and copy the contents into the <b>direct</b> and

     * <b>native ordered</b> java.nio buffer {@link dst}. {@link dst} must have capacity greater than

     * or equal to that of the source Tensor. This operation will not affect dst's content past the

     * source Tensor's size.

     */

    public void fetch(String outputName, IntBuffer dst) {

        getTensor(outputName).writeTo(dst);

    }



    /**

     * Read from a Tensor named {@link outputName} and copy the contents into the <b>direct</b> and

     * <b>native ordered</b> java.nio buffer {@link dst}. {@link dst} must have capacity greater than

     * or equal to that of the source Tensor. This operation will not affect dst's content past the

     * source Tensor's size.

     */

    public void fetch(String outputName, LongBuffer dst) {

        getTensor(outputName).writeTo(dst);

    }



    /**

     * Read from a Tensor named {@link outputName} and copy the contents into the <b>direct</b> and

     * <b>native ordered</b> java.nio buffer {@link dst}. {@link dst} must have capacity greater than

     * or equal to that of the source Tensor. This operation will not affect dst's content past the

     * source Tensor's size.

     */

    public void fetch(String outputName, DoubleBuffer dst) {

        getTensor(outputName).writeTo(dst);

    }



    /**

     * Read from a Tensor named {@link outputName} and copy the contents into the <b>direct</b> and

     * <b>native ordered</b> java.nio buffer {@link dst}. {@link dst} must have capacity greater than

     * or equal to that of the source Tensor. This operation will not affect dst's content past the

     * source Tensor's size.

     */

    public void fetch(String outputName, ByteBuffer dst) {

        getTensor(outputName).writeTo(dst);

    }

    public Tensor fetch(String outputName) {
        return getTensor(outputName);
    }



    private void prepareNativeRuntime() {

        Log.i(TAG, "Checking to see if TensorFlow native methods are already loaded");

        try {

            // Hack to see if the native libraries have been loaded.

            new RunStats();

            Log.i(TAG, "TensorFlow native methods already loaded");

        } catch (UnsatisfiedLinkError e1) {

            Log.i(

                    TAG, "TensorFlow native methods not found, attempting to load via tensorflow_inference");

            try {

                System.loadLibrary("tensorflow_inference");

                Log.i(TAG, "Successfully loaded TensorFlow native methods (RunStats error may be ignored)");

            } catch (UnsatisfiedLinkError e2) {

                throw new RuntimeException(

                        "Native TF methods not found; check that the correct native"

                                + " libraries are present in the APK.");

            }

        }

    }



    private void loadGraph(byte[] graphDef, Graph g) throws IOException {

        final long startMs = System.currentTimeMillis();



        if (VERSION.SDK_INT >= 18) {

            Trace.beginSection("importGraphDef");

        }



        try {

            g.importGraphDef(graphDef);

        } catch (IllegalArgumentException e) {

            throw new IOException("Not a valid TensorFlow Graph serialization: " + e.getMessage());

        }



        if (VERSION.SDK_INT >= 18) {

            Trace.endSection(); // importGraphDef.

        }



        final long endMs = System.currentTimeMillis();

        Log.i(

                TAG,

                "Model load took " + (endMs - startMs) + "ms, TensorFlow version: " + TensorFlow.version());

    }



    private void addFeed(String inputName, Tensor<?> t) {

        // The string format accepted by TensorFlowInferenceInterface is node_name[:output_index].

        TensorId tid = TensorId.parse(inputName);

        runner.feed(tid.name, tid.outputIndex, t);

        feedNames.add(inputName);

        feedTensors.add(t);

    }



    private static class TensorId {

        String name;

        int outputIndex;



        // Parse output names into a TensorId.

        //

        // E.g., "foo" --> ("foo", 0), while "foo:1" --> ("foo", 1)

        public static TensorId parse(String name) {

            TensorId tid = new TensorId();

            int colonIndex = name.lastIndexOf(':');

            if (colonIndex < 0) {

                tid.outputIndex = 0;

                tid.name = name;

                return tid;

            }

            try {

                tid.outputIndex = Integer.parseInt(name.substring(colonIndex + 1));

                tid.name = name.substring(0, colonIndex);

            } catch (NumberFormatException e) {

                tid.outputIndex = 0;

                tid.name = name;

            }

            return tid;

        }

    }



    private Tensor<?> getTensor(String outputName) {

        int i = 0;

        for (String n : fetchNames) {

            if (n.equals(outputName)) {

                return fetchTensors.get(i);

            }

            ++i;

        }

        throw new RuntimeException(

                "Node '" + outputName + "' was not provided to run(), so it cannot be read");

    }



    private void closeFeeds() {

        for (Tensor<?> t : feedTensors) {

            t.close();

        }

        feedTensors.clear();

        feedNames.clear();

    }



    private void closeFetches() {

        for (Tensor<?> t : fetchTensors) {

            t.close();

        }

        fetchTensors.clear();

        fetchNames.clear();

    }
}
