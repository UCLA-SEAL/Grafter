# Grafter
Automated Transplantation and Differential Testing for Clones (ICSE 2017)

## Summary of Grafter 
Code clones are common in software. When applying similar edits to clones, developers often find it difficult to examine the runtime behavior of clones. The problem is exacerbated when some clones are tested, while their counterparts are not. To reuse tests for similar but not identical clones, Grafter transplants one clone to its counterpart by (1) identifying variations in identifier names, types, and method call targets, (2) resolving compilation errors caused by such variations through code transformation, and (3) inserting stub code to transfer input data and intermediate output values for examination. To help developers examine behavioral differences between clones, Grafter supports fine-grained differential testing at both the test outcome level and the intermediate program state level.
In our evaluation on three open source projects, Grafter successfully reuses tests in 94% of clone pairs without inducing build errors, demonstrating its automated code transplantation capability. To examine the robustness of Grafter, we systematically inject faults using a mutation testing tool, Major, and detect behavioral differences induced by seeded faults. Compared with a static cloning bug finder, Grafter detects 31% more mutants using the test-level comparison and almost 2X more using the state-level comparison. This result indicates that Grafter should effectively complement static cloning bug finders.

## Team 
This project is developed by Professor [Miryung Kim](http://web.cs.ucla.edu/~miryung/)'s Software Engineering and Analysis Laboratory at UCLA. 
If you encounter any problems, please open an issue or feel free to contact us:

[Tianyi Zhang](https://https://tianyi-zhang.github.io): was a PhD student at UCLA and now an assistant professor at Purdue; tianyi@purdue.edu

[Miryung Kim](http://web.cs.ucla.edu/~miryung/): Professor at UCLA; miryung@cs.ucla.edu

## How to cite 
Please refer to our ICSE'17 paper, [Automated transplantation and differential testing for clones](http://web.cs.ucla.edu/%7Etianyi.zhang/grafter.pdf) for more details. 

### Bibtex  
@inproceedings{10.1109/ICSE.2017.67,
author = {Zhang, Tianyi and Kim, Miryung},
title = {Automated Transplantation and Differential Testing for Clones},
year = {2017},
isbn = {9781538638682},
publisher = {IEEE Press},
url = {https://doi.org/10.1109/ICSE.2017.67},
doi = {10.1109/ICSE.2017.67},
booktitle = {Proceedings of the 39th International Conference on Software Engineering},
pages = {665–676},
numpages = {12},
location = {Buenos Aires, Argentina},
series = {ICSE '17}
}

[DOI Link](https://dl.acm.org/doi/10.1109/ICSE.2017.67)

## Slides

You can find ICSE 2017 slides [here](http://web.cs.ucla.edu/~miryung/Publications/icse2017-grafter-slides.pdf). 


## Demo Paper 
You can find our demo paper [here](https://dl.acm.org/doi/10.1145/3183440.3195038). 
