//
//  AboutViewController.m
//  Banda Beat
//
//  Created by iker on 20/11/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "HTMLViewController.h"

@interface HTMLViewController ()

@end

@implementation HTMLViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
     id<GAITracker> tracker = [[GAI sharedInstance] defaultTracker];
    
    [tracker trackEventWithCategory:@"Help"
                         withAction:@"Help"
                          withLabel:_fileName
                          withValue:[NSNumber numberWithInt:1]];
    
    NSURL *url = [NSURL fileURLWithPath:[[NSBundle mainBundle] pathForResource:_fileName ofType:@"html"]isDirectory:NO];
    [_web loadRequest:[NSURLRequest requestWithURL:url]];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(IBAction)cancel:(id)sender
{
    [self dismissModalViewControllerAnimated:YES];
}

@end
