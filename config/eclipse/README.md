# Eclipse Code Formatter
Eclipse can be configured to automatically format source code based on a style guide.
It won't catch all the warnings provided by checkstyle so you'll still have to run `./gradlew check` to verify it meets the style guide, however it will make things a lot closer.

# Installing the coding style settings in Eclipse
Under Window/Preferences select `Java > Code Style > Formatter`.
Import the settings file, `java-google-style.xml`, by selecting `Import`.

# Auto-formatting
You can format your code using `⌘ F`, or you can configure eclipse to auto-format when you save.

Under Window/Preferences select `Java > Editor > Save Actions` and make sure the "Perform selected actions on save` checkbox is selected.
Then select the "Format source code" checkbox.
