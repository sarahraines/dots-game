import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class ScoreScanner {
    private Map<String, String> scores = new TreeMap<>();
    private BufferedReader br;

    public static class FormatException extends Exception {
        public FormatException(String msg) {
            super(msg);
        }
    }

    public ScoreScanner(Reader r) throws IOException, FormatException {
        if (r == null) {
            throw new IllegalArgumentException();
        }
        br = new BufferedReader(r);
        while (br.ready()) {
            String line = br.readLine();
            line = line.toUpperCase();
            String bef = new String();
            String aft = new String();
            if (line.indexOf(",") == -1) {
                throw new ScoreScanner.FormatException("Invalid line: no comma");
            } else if (line.indexOf(",") != line.lastIndexOf(",")) {
                throw new FormatException("Invalid line: multiple commas");
            }
            for (int i = 0; i < line.indexOf(","); i++) {
                String str = new String();
                str += line.charAt(i);
                if (!str.equals(" ")) {
                    bef += line.charAt(i);
                }
            }
            for (int i = line.indexOf(",") + 1; i < line.length(); i++) {
                String str = new String();
                str += line.charAt(i);
                if (!str.equals(" ")) {
                    aft += line.charAt(i);
                }
            }
            if (bef.equals("")) {
                throw new ScoreScanner.FormatException("Invalid line: user not available");
            }
            if (aft.equals("")) {
                throw new ScoreScanner.FormatException("Invalid line: score not available");
            } else if (!scores.containsKey(bef)) {
                scores.put(bef, aft);
            }
            else {
                int oldScore = Integer.parseInt(scores.get(bef));
                if (Integer.parseInt(aft) > oldScore) {
                    scores.put(bef, aft);
                }
            }
        }
    }

    public static ScoreScanner make(String filename) throws IOException, FormatException {
        Reader r = new FileReader(filename);
        ScoreScanner ss;

        try {
            ss = new ScoreScanner(r);
        } finally {
            if (r != null) {
                r.close();
            }
        }

        return ss;
    }

    public String getScores() {
        String labelText = "<html><h2>High Scores</h2>";
        for (int i = 0; i < 10; i++) {
            if (scores.size()!= 0) {
                int baseScore = -1;
                String highScorer = "";
                for (Map.Entry<String, String> score: scores.entrySet()) {
                    int scoreNum = Integer.parseInt(score.getValue());
                    if (scoreNum > baseScore) {
                        baseScore = scoreNum;
                        highScorer = score.getKey();
                    }
                }
                labelText += highScorer + ": " + scores.get(highScorer) + "<br>";
                scores.remove(highScorer);
            }
        }

        labelText+= "That's all folks!</html>";
        return labelText;
    }
    public int scoresNum() {
        return scores.size();
    }

    public Map<String, String> getScoresMap() {
        return scores;
    }

}