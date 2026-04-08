const pup = require('puppeteer');
const fs = require('fs');
const path = require('path');
const cheerio = require('cheerio');

async function fetchAllWords(browser, entry) {
    const page = await browser.newPage();
    await page.goto('https://www.merriam-webster.com/dictionary/'+entry);

    const html = await page.content();    

    const $ = cheerio.load(html);
    const words = new Set();
    const text = $("body").text();
    const wordsr = text.split(" ");
    for (const word of wordsr){
    if(!words.has(word))words.add(word.replace(/[^a-zA-Z]/g, ''));
    }
    await browser.close();
    return words;
}

async function main(){
    const browser = await pup.launch();
    const entries = ['launch', 'fetch', 'word', 'definition', 'scrape', 'castle', 'ocean', 'mountain', 'river', 'forest'];
    for (const entry of entries){
        try{
        console.log(`Fetching words related to: ${entry}`);
    
    const words = await fetchAllWords(browser, entry);
    for (const word of words){
        try{
        console.log("Downloading word:", word);
        await downloadEachWord(word, await pup.launch());
        }catch(e){
            console.error(`Error downloading word ${word}:`, e);
        }
    }
        }catch(e){
            console.error(`Error fetching words for entry ${entry}:`, e);
        }
}
await browser.close();
}
async function downloadEachWord(word, browser){
    const page = await browser.newPage();
    await page.goto('https://www.merriam-webster.com/dictionary/'
        + word.toLowerCase());
    const html = await page.content();
    const $ = cheerio.load(html);
    const definition = $('.dtText').first().text().trim();
    const wordData = {
        word: word,
        definition: definition
    };
    
    //const filePath = path.join(__dirname, 'words', `${word}.txt`);
   // fs.writeFileSync(filePath);
    const filePath = path.join(__dirname, 'words', `${word}.json`);
    fs.writeFileSync(filePath, JSON.stringify(wordData, null, 2));
}
    
main();