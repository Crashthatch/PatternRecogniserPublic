package database;

import difflib.DiffUtils;
import difflib.Patch;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Tom on 27/07/2014.
 */
public class DiffCallable implements Callable<Patch> {
    private List<Character> fromChars;
    private List<Character> toChars;

    public DiffCallable( List<Character>  fromChars, List<Character> toChars){
        this.fromChars = fromChars;
        this.toChars = toChars;
    }

    public Patch call() {
        return DiffUtils.diff(fromChars, toChars);
    }


}
