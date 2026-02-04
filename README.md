<div align="center">

# YTSummary

</div>

A backend-focused system that accepts a YouTube URL, extracts the video transcript, processes it in token-safe chunks, and generates a consolidated summary via an LLM provider.
The project emphasizes clean architecture, extensibility, and production-ready patterns.

## ⚡ Features

* YouTube video fetching
* Transcript extraction
* LLM-powered summarization
* Configurable summarization strategy and model

## 👋 Getting Started

Get the code

```bash
git clone https://github.com/ricardoliveira5ro/YTSummary.git
```

Add `OPENAI_API_TOKEN` and `OPENAI_API_MODEL` environment variables as well as `spring.profiles.active` vm option

Access the exposed endpoint

```bash
curl -X POST "http://localhost:8080/api/summarize?ytUrl=<URL>"
```

Output example
```json
{
"summary": "The video explores deep-seated conspiracy theories linked to government secrecy, intelligence operations, financial scandals, and elite corruption. The speaker discusses the recent release of government files, emphasizing that internal documents from the Justice Department, FBI, CIA, and other agencies have been accessible through Freedom of Information Act requests, revealing previously classified or redacted information that implicates prominent figures, multinational corporations, and foreign entities in illegal activities. The files reveal connections between intelligence agencies and organized crime, significant covert operations during the Cold War, and illicit financial networks involving the Vatican Bank, BCCI, and offshore banking hubs like the Cayman Islands and Switzerland. The speaker highlights the longstanding collaboration between intelligence services, mafia organizations, and global financial institutions to conduct black ops, drug trafficking, money laundering, and geopolitical influence campaigns.  The discussion ties prominent figures such as Jeffrey Epstein, Bill Barr, and Adnan Khashoggi to clandestine activities involving covert financing, blackmail, and international power dynamics. The speaker argues that Epstein's network reflects larger systemic issues of government corruption, secret operations, and elite complicity that remain largely hidden due to classified documents, legal protections, and deliberate cover-ups. They advocate for congressional intervention, including similar declassification laws used in the JFK files case, to uncover the truth and expose the profound influence of secret networks on current geopolitical and domestic policy. Additionally, the video delves into topics like global climate policies, recent surveillance initiatives, and government overreach, warning that these are part of a broader agenda of control and manipulation designed to benefit elite interests. The speaker expresses skepticism about official narratives and emphasizes the importance of rigorous investigation, open access to classified information, and public awareness to safeguard democracy. ",
"keyWords": "Government secrecy, intelligence agencies, classified files, Epstein network, covert operations, organized crime, financial scandals, BCCI, offshore banking, geopolitical influence, deep state, declassification law."
}
```

## 📁 Project Structure

```
📁 src/
├── 📂 api/
│   ├── 📂 dto/
│   │   └── 📄 SummaryDTO.java
│   └── 📄 YTSummaryController.java
├── 📂 config/
│   └── 📄 HttpClientConfig.java
├── 📂 domain/
│   ├── 📂 model/     
│   │   └── 📄 Transcript.java
│   ├── 📂 port/     
│   │   ├── 📄 SummaryProvider.java     
│   │   └── 📄 TranscriptProvider.java
│   └── 📂 service/     
│       ├── 📄 SummaryService.java     
│       └── 📄 TranscriptService.java
├── 📂 exception/
│   ├── 📄 ErrorResponse.java 
│   ├── 📄 GlobalExceptionHandler.java
│   ├── 📄 InvalidUrlException.java               
│   └── 📄 ...  
├── 📂 infrastructure/
│   ├── 📂 openai/     
│   │   ├── 📄 OpenAIClient.java
│   │   ├── 📄 OpenAIResponseParser.java
│   │   └── 📄 OpenAISummaryProvider.java 
│   └── 📂 youtube/     
│       ├── 📄 YouTubeCaptionParser.java
│       ├── 📄 YouTubeClient.java
│       ├── 📄 YouTubeHtmlParser.java
│       └── 📄 YouTubeTranscriptProvider.java
├── 📂 resources/
│   └── 📄 application.yml                                      
└── 📄 pom.xml
```