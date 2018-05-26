#!/bin/bash

# setup dev environment

sudo apt-get install postgresql postgresql-contrib
sudo -u postgres createuser --interactive
