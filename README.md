# berta-pull

This is a second part of my "Berta" project - a Change Impact Analysis tool.

This part connects to any number of Berta java agents via the text based API.
It exposes its own, more convenient HTTP API (note: it is not a REST API, I did not follow RESTful guide in writing it).

The goal of this part is to gather information from a cluster of Berta agents and push them to a centra location for indexing.

At the moment it only supports local ElasticSearch.

This is still very early in the development and is still on a Proof-of-Concept stage of its life.

Feel free to explore or even give it a try, but there is no reason to use it in production.

