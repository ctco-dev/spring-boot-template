## Docker installation issues

Installation of Docker turned out to be a challenge on my home PC.  
It might be worth giving more guidance or support for this step in future trainings.

## Spotless formatting errors

I faced an issue where my code looked correct in the IDE, but Spotless (`:spotlessJavaCheck`) kept failing.  
The root cause was not the logic of the code, but **invisible whitespace**:

- My IDE was using 4 spaces for indentation, while Spotless expected 2 spaces.
- Some blank lines contained hidden spaces, which Spotless detected as violations.
- The file was missing a final newline character at the end.

This was confusing at first because the code seemed fine, but formatting rules are very strict.  
The fix was to adjust indentation to 2 spaces, remove trailing whitespace on empty lines, and ensure the file ends with a newline.

### Solution

- Enabled **EditorConfig support** in IntelliJ and added a `.editorconfig` file with:
    - `indent_size = 2`
    - `trim_trailing_whitespace = true`
    - `insert_final_newline = true`
- Configured **Settings → Editor → General → On Save** to automatically remove trailing spaces and ensure a newline at the end of files.
- Installed the **Google Java Format** plugin so that `Ctrl+Alt+L` (Reformat Code) applies the same formatting rules as Spotless.
- Used `gradlew spotlessApply` to fix existing violations.
