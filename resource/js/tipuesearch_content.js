var tipuesearch={"pages":[{"title":"Home \/ Simple codex","text":" Simple codex {&quot;simple&quot; {&quot;path\/data&quot; {:get {}}}} This defines a service with one endpoint \/simple\/path\/data which can be called with a GET method. ","tags":"","loc":"index.html#simple-codex"},{"title":"Home \/ Documentation codex","text":" A codex with some basic documentation {&quot;simple&quot; {&quot;path\/data&quot; {:get {:doc &quot;A simple service&quot;}}}} A doc attribute lets us put basic documentation against many items in a codex. ","tags":"","loc":"index.html#doc-codex"},{"title":"Home \/ Preferences codex","text":" Defining preferences using includes { :includes [ &quot;defaults.edn&quot; ] &quot;simple&quot; { &quot;path\/data&quot; {:get {}} } } Using the defaults include for our 'organisation' or personal preferences we could override the status returned by a get to be 204 or anything we choose. ","tags":"","loc":"index.html#preferences-codex"},{"title":"Home \/ Multiple responses codex","text":" A codex demonstrating multiple responses - possible errors. { :includes [ &quot;defaults.edn&quot; ] &quot;simple&quot; { &quot;path\/data&quot; { :get { :rsp { :200 { :doc &quot;OK&quot; } :400 { :doc &quot;Bad Request&quot; } :404 { :doc &quot;Not Found&quot; } } } } } } All possible responses, good and bad can be documented. ","tags":"","loc":"index.html#multi-response-codex"}]};var tipuedrop=tipuesearch;var tipuesearch_stop_words = ["and", "be", "by", "do", "for", "he", "how", "if", "is", "it", "my", "not", "of", "or", "the", "to", "up", "what", "when"];var tipuesearch_replace = {"words": []};var tipuesearch_stem = {"words": []};