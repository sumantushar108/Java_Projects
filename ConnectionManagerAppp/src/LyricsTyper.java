public class LyricsTyper {

    // Method to type a line character by character
    public static void typeLyric(String line, long charDelay) {
        for (int i = 0; i < line.length(); i++) {
            System.out.print(line.charAt(i));
            System.out.flush();
            try {
                Thread.sleep(charDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }

    // Overloaded method with default delay (like Python default argument)
    public static void typeLyric(String line) {
        typeLyric(line, 65); // 0.065 seconds = 65 ms
    }

    // Method to print all lyrics
    public static void printLyrics() {
        String[] lyrics = {
            "Dil jo tumhara hai,",
            "Kaisa bechara hai,",
            "Maane na besharam, bilkul khatara hai,",
            "Tu kare dil beqaraar,",
            "Kyun karoon main tujhse pyar"
        };

        long[] delays = {1500, 1500, 2000, 1800, 2300};

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (int i = 0; i < lyrics.length; i++) {
            typeLyric(lyrics[i]);
            try {
                Thread.sleep(delays[i]);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        printLyrics();
    }
}
