Feature: Allow Tutorial Producers to initialize their G2Tutorial environment
    This will allow them to start working on their tutorial

Scenario: The Tutorial Producer has a prompt in an empty directory
    Given the user has installed G2Tutorial
    And the user has a prompt in an empty directory
    When the user enters the command `g2t init`
    Then g2tutorial initializes a git repo
