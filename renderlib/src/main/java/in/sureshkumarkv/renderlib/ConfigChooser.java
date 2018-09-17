//package in.sureshkumarkv.renderlib;
//
//import android.opengl.GLSurfaceView;
//
//import javax.microedition.khronos.egl.EGL10;
//import javax.microedition.khronos.egl.EGLConfig;
//import javax.microedition.khronos.egl.EGLDisplay;
//
//class ConfigChooser implements GLSurfaceView.EGLConfigChooser {
//        final private static String TAG = "ConfigChooser";
//
//        // This constant is not defined in the Android API, so we need to do that here:
//        final private static int EGL_OPENGL_ES2_BIT = 4;
//
//        // Our minimum requirements for the graphics context
//        private static int[] mMinimumSpec = {
//                // We want OpenGL ES 2 (or set it to any other version you wish)
//                EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
//
//                // We want to render to a window
//                EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT,
//
//                // We do not want a translucent window, otherwise the
//                // home screen or activity in the background may shine through
//                EGL10.EGL_TRANSPARENT_TYPE, EGL10.EGL_NONE,
//
//                // indicate that this list ends:
//                EGL10.EGL_NONE
//        };
//
//        private int[] mValue = new int[1];
//        protected int mAlphaSize;
//        protected int mBlueSize;
//        protected int mDepthSize;
//        protected int mGreenSize;
//        protected int mRedSize;
//        protected int mStencilSize;
//
//        /**
//         * The constructor lets you specify your minimum pixel format,
//         * depth and stencil buffer requirements.
//         */
//        public ConfigChooser(int r, int g, int b, int a, int depth, int stencil) {
//            mRedSize = r;
//            mGreenSize = g;
//            mBlueSize = b;
//            mAlphaSize = a;
//            mDepthSize = depth;
//            mStencilSize = stencil;
//        }
//
//        @Override
//        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
//            int[] arg = new int[1];
//            egl.eglChooseConfig(display, mMinimumSpec, null, 0, arg);
//            int numConfigs = arg[0];
//
//            if (numConfigs <= 0) {
//                // Ooops... even the minimum spec is not available here
//                return null;
//            }
//
//            EGLConfig[] configs = new EGLConfig[numConfigs];
//            egl.eglChooseConfig(display, mMinimumSpec, configs,
//                    numConfigs, arg);
//
//            // Let's do the hard work now (see next method below)
//            EGLConfig chosen = chooseConfig(egl, display, configs);
//
//            if (chosen == null) {
//                throw new RuntimeException("Could not find a matching configuration");
//            }
//
//            // Success
//            return chosen;
//        }
//
//        /**
//         * This method iterates through the list of configurations that
//         * fulfill our minimum requirements and tries to pick one that matches best
//         * our requested color, depth and stencil buffer requirements that were set using
//         * the constructor of this class.
//         */
//        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display,
//                                      EGLConfig[] configs) {
//            EGLConfig bestMatch = null;
//            int bestR = Integer.MAX_VALUE, bestG = Integer.MAX_VALUE,
//                    bestB = Integer.MAX_VALUE, bestA = Integer.MAX_VALUE,
//                    bestD = Integer.MAX_VALUE, bestS = Integer.MAX_VALUE;
//
//            for (EGLConfig config : configs) {
//                int r = findConfigAttrib(egl, display, config,
//                        EGL10.EGL_RED_SIZE, 0);
//                int g = findConfigAttrib(egl, display, config,
//                        EGL10.EGL_GREEN_SIZE, 0);
//                int b = findConfigAttrib(egl, display, config,
//                        EGL10.EGL_BLUE_SIZE, 0);
//                int a = findConfigAttrib(egl, display, config,
//                        EGL10.EGL_ALPHA_SIZE, 0);
//                int d = findConfigAttrib(egl, display, config,
//                        EGL10.EGL_DEPTH_SIZE, 0);
//                int s = findConfigAttrib(egl, display, config,
//                        EGL10.EGL_STENCIL_SIZE, 0);
//
//                if (r <= bestR && g <= bestG && b <= bestB && a <= bestA
//                        && d <= bestD && s <= bestS && r >= mRedSize
//                        && g >= mGreenSize && b >= mBlueSize
//                        && a >= mAlphaSize && d >= mDepthSize
//                        && s >= mStencilSize) {
//                    bestR = r;
//                    bestG = g;
//                    bestB = b;
//                    bestA = a;
//                    bestD = d;
//                    bestS = s;
//                    bestMatch = config;
//                }
//            }
//
//            System.out.println("--Chosen config: " + bestR + " , " + bestG + " , " + bestB + " , " + bestA + " , " + bestD + " , " + bestS);
//
//            return bestMatch;
//        }
//
//        private int findConfigAttrib(EGL10 egl, EGLDisplay display,
//                                     EGLConfig config, int attribute, int defaultValue) {
//
//            if (egl.eglGetConfigAttrib(display, config, attribute,
//                    mValue)) {
//                return mValue[0];
//            }
//
//            return defaultValue;
//        }
//    }