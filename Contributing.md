# Contributing to Dynamic Relations

We encourage and appreciate feedback and contribution from the community!

- [Question or Problem](#question)
- [Issue and Feature Requests](#issue)
- [PR Submission Guidelines](#submit-pr)
- [Coding Rules](#rules)
- [Set Up](#setup)

## <a name="question"></a> Question or Problem?

Feel free to open an Issue with an Question or Bug Report.

## <a name="issue"></a> Issue and Feature Requests

Search [Github Issues](https://github.com/Mom0aut/DynamicRelations/issues) for existing bugs report or features request related to your question.
Please submit an Issue or Feature Request if your issues or requests have not been listed.

## <a name="submit-pr"></a> PR Submission Guidelines

- Search Github [Pull Requests](https://github.com/Mom0aut/DynamicRelations/pulls) for PRs related to your submission. Make sure that this is not a duplication.
- Link the issue addressed by the PR.
- Add unit tests or document manual tests to validate the changes.
- Workflow shall triggers all unit tests. For a pull request to be accepted, all unit tests must be green.

## <a name="rules"></a> Coding Rules

Please follow the rules as you work on the code:

- Please add unit tests for each fixed bug or added feature.
- Please use clean and informative names.
- Leave the code better than you find it.
- Use the given Style Guide [Style Guide](https://github.com/Mom0aut/DynamicRelations/blob/master/StyleGuide.xml)

##  <a name="setup"></a> Set Up
When starting out, make sure to check these few things before building the project:

- Ensure that Annotation Processor is enabled in your IDE.
- Ensure that the processor FQ name is added and set to:
  ```at.drm.processor.RelationProcessor```.
- For IntelliJ, ensure you use the inbuilt Build tool found at the 'Build' tab. Running ```mvn clean install``` 
in the CLI will NOT have Annotation Processors activated which is required for this project.

Happy contributing :smiley:
