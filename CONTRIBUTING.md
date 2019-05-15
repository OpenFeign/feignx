Contributing
---
Thank you for considering contributing to Feignx.

The following is a set of guidelines for contributing to Feignx and its packages.  Following these
guidelines helps to communicate that you respect the time of the maintainers, reviewers, and other
contributors of this project.  These guidelines are intended to be used as that, guidelines, so
use your best judgement.  If you have any questions, feel free to [raise an issue](https://github.com/openfeign/feignx/issues)
and we will do our best to help.

Code of Conduct
---
This project and everyone involved with it, is governed by the 
[Contributor Covenant Code of Conduct](https://www.contributor-covenant.org/version/1/4/code-of-conduct.md).  By participating, you are expected to uphold this code.
Please report any unacceptable behavior to the maintainers.

How can I contribute?
---

Reporting Issues
---
> **Please do not create an issue for a question**, see [Getting Help](#Getting-Help) 
for more information.

### Security Issues

The Feign team take security issue very seriously.  All security issues should be reported to the maintainers directly at [insert email here]().
A maintainer will respond to your request.

Report security issues for third-party libraries to the person or team maintaining that library.  

### Bug Reports

When filing an issue, make sure to answer the following questions:

* What version of Feignx are you using?
* What JDK version are you using?
* Are you using any extensions?  Examples include Spring Cloud, Hystrix.
* What did you do?
* What did you expect to see?
* What did you see instead?
* Are you able to reproduce it?

Open a new "Bug Report" issue and include your answers to these questions.  Maintainers periodically
review these requests and will provide additional feedback.

### Feature Requests

If you find yourself looking for a feature that does not exist, or have suggestions on how to make 
Feignx better, chances are you are not alone.  Many features have come from community members and 
added because our users saw the value.  Open a "Feature Request" issue and describe the feature 
you would like to see and why.  Here are few additional items to include in your request:

* A clear and descriptive title.
* Describe the current behavior and how this feature may alter that behavior.
* An explanation on why this feature will be useful.  Include as much detail as desired.

### What happens now?

Look for feedback on your suggestion from maintainers and others from the community.  In some cases
a workaround may be provided.  In others, more feedback may be required before a decision is made.
Feignx subscribes to the philosophy of the [Rule of Three](https://blog.codinghorror.com/rule-of-three/) 
when prioritizing new features, allowing the maintainers to focus on the most requested ones first.

Your First Contribution
---
Unsure of where to start?  You can start by looking at our `good-first-issue` and `help-wanted` issues.

* `good-first-issue`: Issues are selected specifically for those who may not have contributed before.
* `help-wanted`: Issues where the we are looking for a member of the community to take on and help close.

Once you've found an issue, please be sure to assign it to yourself.  This will help the maintainers know
which issues are being worked on and which ones need attention.

### Fork the repository

You will need to fork this repository to make changes and submit pull requests.  See the GitHub help
pages on how to [Fork a Repository](https://help.github.com/en/articles/fork-a-repo).  Make all your 
updates in your own copy and be sure to [use good commit messages](https://chris.beams.io/posts/git-commit/).

### Submitting your changes
 
You've squashed that bug, updated that documentation, or implemented that amazing new feature the entire
community has been looking for.  Now, to get that change submitted so the maintainers can review and provide
feedback.

1.  **Test your changes**
    
    Ensure that all of the existing test cases, and the ones you've added, pass by running:

     ```
     ./mvnw clean test
     ```
     
     This command will validate that the code complies with our code styles, license requirements, compiles
     and passes all of the test cases.  
     
     **Code Style Violations**
     
     Feignx follows the [Google Style Guide](https://google.github.io/styleguide/javaguide.html).  Please ensure that your changes
     adhere to these guidelines.
     The build will report any violations and how to correct them.
     
     **License Violations**
     
     Feignx requires that all source files contain the license NOTICE header.  To ensure that your changes
     are accepted, use:
     
     ```
     ./mvnw license:format
     ```
     
     This command will ensure that all of the source files have the correct NOTICE header.

2.  **Submit a pull request**

     Push your changes to your local repository and [submit a Pull Request](https://help.github.com/articles/using-pull-requests), 
     using the template provided.
     
 Expect feedback from the maintainers and possibly other contributors.  If any changes are needed, we will
 work with you to get your pull request ready and merged.
 
### Making updates to an open Pull Request.
 
1.  Please do not **force push** `--force` changes to a branch with an open pull request.  Once shared, 
please consider the branch public.  
2.  Add incremental commits, [using good commit message practices](https://chris.beams.io/posts/git-commit/).
this makes it easier for reviewers to keep track of the changes.
3.  Refrain from performing a rebase or merging the baseline branch into the PR unless asked to do so.
Like with force pushes, rebasing and merging an open branch make reviewing the changes difficult.  It is also
our practice to squash all commits when merging a pull request, so rebasing or merging should be unnecessary.
 
### When your change is accepted.
 
Celebrate :tada:, you've just become part of the Open Source community.
 
Getting Help
---

You can chat with the maintainers and contributors on [Gitter](https://gitter.im) in the
[Feign Channel](https://gitter.im/OpenFeign/feign).  This is the best way to reach the maintainers if
you need immediate feedback.

### General Questions?
 
For general questions, please use [Stack Overflow, using the `feign` tag](https://stackoverflow.com/questions/tagged/feign).
Maintainer check this tag regularly and will do their best to respond promptly.  
