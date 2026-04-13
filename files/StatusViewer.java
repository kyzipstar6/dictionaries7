import javax.swing.*;
import java.io.*;
import java.awt.Dimension;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class StatusViewer extends JFrame{
    public StatusViewer(){
        setTitle("Status Viewer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JTextArea textArea = new JTextArea("Fetching data...");
        
        textArea.setPreferredSize(new Dimension(400,300));

        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane();
        ProcessBuilder processBuilder = new ProcessBuilder("node", "fetchAllWords.js");
        scrollPane.setPreferredSize(new Dimension(400,300));
        JButton startButton = new JButton("Start Fetching");
        JButton stopButton = new JButton("Stop Fetching");
        JButton configureButton = new JButton("Configure JS Script");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(configureButton);
        add(buttonPanel, "North");
        startButton.addActionListener(e -> {
            new Thread(() -> {
                try {
                    Process process = processBuilder.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String finalLine = line;
                        SwingUtilities.invokeLater(() -> textArea.append(finalLine + "\n"));
                    }
                    process.waitFor();
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
        stopButton.addActionListener(e -> {
            // Implement logic to stop the fetching process if needed
        });

        configureButton.addActionListener(e -> {
            String newLang = JOptionPane.showInputDialog(this, "Enter a language (english, french, german, spanish, italian, portuguese, russian, japanese, chinese, arabic):");
            configureJSCript(newLang);
        });
        add(textArea);
        pack();
        setVisible(true);
        
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            StatusViewer viewer = new StatusViewer();
            viewer.setVisible(true);
        });
    }
    public void configureJSCript(String lang){
        try{
        String [] englishWords = {"hello", "thank you", "love",
                "launch", "fetch", "word", "definition", "scrape", "castle", "ocean", "mountain", "river", "forest"
            };
        String [] frenchWords = {"bonjour", "merci", "amour", "lancer", "chercher", "mot", "définition", "gratter", "château", "océan", "montagne", "rivière", "forêt"};
        String [] germanWords = {"hallo", "danke", "liebe", "starten", "holen", "wort", "definition", "kratzen", "schloss", "ozean", "berg", "fluss", "wald"};
        String [] spanishWords = {"hola", "gracias", "amor", "lanzar", "obtener", "palabra", "definición", "rascar", "castillo", "océano", "montaña", "río", "bosque"};
        String [] italianWords = {"ciao", "grazie", "amore", "lanciare", "ottenere", "parola", "definizione", "grattare", "castello", "oceano", "montagna", "fiume", "foresta"};
        String [] portugueseWords = {"olá", "obrigado", "amor", "lançar", "obter", "palavra", "definição", "raspar", "castelo", "oceano", "montanha", "rio", "floresta"};
        String [] russianWords = {"привет", "спасибо", "любовь", "запускать", "получать", "слово", "определение", "царапать", "замок", "океан", "гора", "река", "лес"};
        String [] japaneseWords = {"こんにちは", "ありがとう", "愛", "開始", "取得", "単語", "定義", "スクレイプ", "城", "海洋", "山", "川", "森"};
        String [] chineseWords = {"你好", "谢谢", "爱", "启动", "获取", "单词", "定义", "抓取", "城堡", "海洋", "山", "河流", "森林"};
        String [] arabicWords = {"مرحبا", "شكرا", "حب", "إطلاق", "جلب", "كلمة", "تعريف", "كشط", "قلعة", "محيط", "جبل", "نهر", "غابة"};
            String [] languages = {"english", "french", "german", "spanish", "italian", "portuguese", "russian", "japanese", "chinese", "arabic"};
            String [][] allWords = {englishWords, frenchWords, germanWords, spanishWords, italianWords, portugueseWords, russianWords, japaneseWords, chineseWords, arabicWords};

            String [] dictionaryWebsites = {"https://www.merriam-webster.com/dictionary/", "https://www.larousse.fr/dictionnaires/francais/", "https://www.duden.de/suchen/dudenonline/", "https://www.rae.es/", "https://www.treccani.it/vocabolario/", "https://www.priberam.pt/dlpo/",
             "https://slovari.yandex.ru/", "https://jisho.org/", "https://www.zdic.net/", "https://www.almaany.com/ar/dict/ar-en/"};
             List<String> dictionaryWebsitesList = Arrays.asList(dictionaryWebsites);
             List<String> languagesList = Arrays.asList(languages);
        
            String nodescript = new String(Files.readAllBytes(new File("fetchAllWords.js").toPath()));
        StringBuilder sb = new StringBuilder();
        sb.append("const entries = [");
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].equalsIgnoreCase(lang)) {
                for (String word : allWords[i]) {
                    sb.append("'").append(word).append("',");
                }
                break;
            }
        }
        sb.append("];\n");
        StringBuilder finalScript = new StringBuilder();

        String cannotcontain = "const entries = [";
        String[] lines = nodescript.split("\n");
        for (String line : lines) {
            for(int i = 0; i < dictionaryWebsites.length; i++) {
                if (line.contains(dictionaryWebsites[i])) {
                    line = line.replace(dictionaryWebsites[i], dictionaryWebsitesList.get(languagesList.indexOf(lang.toLowerCase())));
                    break;
                }
            }
            String finalLine = line.replace(dictionaryWebsites[0], dictionaryWebsitesList.get(languagesList.indexOf(lang.toLowerCase())));

            if (line.contains(cannotcontain)) {
                
                finalScript.append(sb.toString().replace(",]", "]").replace("words", lang.toLowerCase() + "Words")).append("\n");
            } else {
                finalScript.append(finalLine.replace("words", lang.toLowerCase() + "Words")).append("\n");
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("fetchAllWords.js"))) {
            writer.write(finalScript.toString());
        }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}