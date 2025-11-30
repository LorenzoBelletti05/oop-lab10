package it.unibo.mvc;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public final class DrawNumberApp implements DrawNumberViewObserver {   

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final DrawNumberView... views) {

        Configuration conf = new Configuration.Builder().build();
        
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        this.model = new DrawNumberImpl(conf.getMin(), conf.getMax(), conf.getAttempts());
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * @param args
     *            ignored
     * @throws FileNotFoundException 
     */
    public static void main(final String... args) throws FileNotFoundException {
        DrawNumberView graphicalView1 = new DrawNumberViewImpl(); // La tua view Swing esistente
        DrawNumberView graphicalView2 = new DrawNumberViewImpl(); // La seconda view Swing
        DrawNumberView fileLogger = new PrintStreamView("output.log");
        // 3. Crea la View su Console (Standard Output) usando la tua PrintStreamView
        DrawNumberView consoleView = new PrintStreamView(System.out);

        new DrawNumberApp(
        graphicalView1, 
        graphicalView2, 
        fileLogger, 
        consoleView
    );
    }

}
